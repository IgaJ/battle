package com.example.battle.services;

import com.example.battle.config.BoardConfiguration;
import com.example.battle.config.WebSocketHandler;
import com.example.battle.mapers.CommandMapper;
import com.example.battle.exceptions.BattleGameException;
import com.example.battle.mapers.GameMapper;
import com.example.battle.model.Game;
import com.example.battle.model.GameDTO;
import com.example.battle.model.Position;
import com.example.battle.model.commands.Command;
import com.example.battle.model.commands.CommandDTO;
import com.example.battle.model.commands.Direction;
import com.example.battle.model.units.*;
import com.example.battle.repositories.GameRepository;
import com.example.battle.repositories.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CommandService {

    private final GameRepository gameRepository;
    private final UnitRepository unitRepository;
    private final GameService gameService;
    private final BoardConfiguration boardConfiguration;
    private final CommandMapper commandMapper;
    private final GameMapper gameMapper;
    private final WebSocketHandler webSocketHandler;


    @Transactional
    public GameDTO move(String color, CommandDTO commandDTO) {
        Optional<Game> gameOptional = gameRepository.findById(commandDTO.getGameId());
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            Unit unit = game.getUnits().stream().filter(u -> Objects.equals(u.getId(), commandDTO.getUnitId())).findFirst().orElse(null);

            if (unit != null && unit.getUnitStatus() == UnitStatus.ACTIVE) {
                if (cannotExecuteCommand(commandDTO.getLastCommand(), unit.getRequiredInterval("move"))) {
                    throw new BattleGameException("Too early to execute command. Wait");
                }
                if (!unit.getColor().equals(color)) {
                    throw new BattleGameException("Incorrect player color");
                }
                Position newPosition = calculateNewPosition(
                        unit.getPosition(),
                        commandDTO.getDirection(),
                        commandDTO.getVerticalSteps(),
                        commandDTO.getHorizontalSteps()
                );

                if (isPositionValid(newPosition, boardConfiguration.getWidth(), boardConfiguration.getHeight())) {
                    Optional<Unit> targetUnitOptional = game.getUnits().stream()
                            .filter(found -> found.getPosition().equals(newPosition))
                            .findFirst();
                    if (targetUnitOptional.isPresent()) {
                        Unit targetUnit = targetUnitOptional.get();
                        if (!targetUnit.getColor().equals(unit.getColor())) {
                            targetUnit.setUnitStatus(UnitStatus.DESTROYED);
                            unit.setPosition(newPosition);
                            game.getUnits().remove(targetUnit);
                            gameRepository.save(game);
                            unitRepository.delete(targetUnit);
                            if (game.getUnits().stream().noneMatch(u -> u.getColor().equals(targetUnit.getColor()) &&
                                    u.getUnitStatus() == UnitStatus.ACTIVE)) {
                                endGame(targetUnit.getColor(), game);
                            }
                        } else {
                            throw new BattleGameException("The vehicle cannot invade its own unit");
                        }
                    } else {
                        unit.setPosition(newPosition);
                        unit.setMoveCount(unit.getMoveCount() + 1);
                    }
                    Command command = commandMapper.map(commandDTO);
                    command.setLastCommand(LocalDateTime.now());
                    game.getCommandHistory().add(command);
                    unitRepository.save(unit);
                    gameService.printBoard();
                } else {
                    throw new BattleGameException("Incorrect new position");
                }
            }
        }
        try {
            webSocketHandler.broadcast("Unit moved: " + commandDTO.getUnitId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return gameMapper.map(gameRepository.findById(commandDTO.getGameId()).orElseThrow(() -> new BattleGameException("Game not found with id " + commandDTO.getGameId())));
    }

    @Transactional
    public GameDTO fire(String color, CommandDTO commandDTO) {
        Optional<Game> gameOptional = gameRepository.findById(commandDTO.getGameId());
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            Unit unit = game.getUnits().stream().filter(u -> Objects.equals(u.getId(), commandDTO.getUnitId())).findFirst().orElse(null);
            if (unit != null && unit.getUnitStatus() == UnitStatus.ACTIVE) {
                if (cannotExecuteCommand(commandDTO.getLastCommand(), unit.getRequiredInterval("fire"))) {
                    throw new BattleGameException("Too early to execute command. Wait");
                }

                if (!unit.getColor().equals(color)) {
                    throw new BattleGameException("Incorrect player color");
                }

                Position newPosition = calculateNewPosition(
                        unit.getPosition(),
                        commandDTO.getDirection(),
                        commandDTO.getVerticalSteps(),
                        commandDTO.getHorizontalSteps()
                );
                Optional<Unit> targetUnitOptional = game.getUnits().stream()
                        .filter(found -> found.getPosition().equals(newPosition))
                        .findFirst();

                if (targetUnitOptional.isPresent()) {
                    Unit targetUnit = targetUnitOptional.get();
                    targetUnit.setUnitStatus(UnitStatus.DESTROYED);
                    game.getUnits().remove(targetUnit);
                    gameRepository.save(game);
                    unitRepository.delete(targetUnit);
                    if (game.getUnits().stream().noneMatch(u -> u.getColor().equals(targetUnit.getColor()) && u.getUnitStatus() == UnitStatus.ACTIVE)) {
                        endGame(targetUnit.getColor(), game);
                    }
                }
                Command command = commandMapper.map(commandDTO);
                command.setLastCommand(LocalDateTime.now());
                game.getCommandHistory().add(command);
                unitRepository.save(unit);
                gameService.printBoard();
            }
        }
        try {
            webSocketHandler.broadcast("Unit was firing: " + commandDTO.getUnitId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return gameMapper.map(gameRepository.findById(commandDTO.getGameId()).orElseThrow(() -> new BattleGameException("Game not found with id " + commandDTO.getGameId())));
    }

    public GameDTO randomMove(String color, CommandDTO commandDTO) {
        Optional<Game> gameOptional = gameRepository.findById(commandDTO.getGameId());
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            Unit unit = game.getUnits().stream().filter(u -> Objects.equals(u.getId(), commandDTO.getUnitId())).findFirst().orElse(null);

            if (unit != null & unit.getUnitStatus() == UnitStatus.ACTIVE) {
                CommandDTO dto = generateRandomMove(commandDTO.getGameId(), commandDTO.getUnitId());
                if (unit instanceof Archer) {
                    if (new Random().nextBoolean()) {
                        move(color, dto);
                    } else {
                        fire(color, dto);
                    }
                } else if (unit instanceof Transport) {
                    move(color, dto);
                } else if (unit instanceof Cannon) {
                    fire(color, commandDTO);
                }
            }
        }
        return gameMapper.map(gameRepository.findById(commandDTO.getGameId()).orElseThrow(() -> new BattleGameException("Game not found with id " + commandDTO.getGameId())));
    }

    private void endGame(String losingColor, Game game) {
        String winningColor = losingColor.equals("w") ? "b" : "w";
        game.setActive(false);
        gameRepository.save(game);
        throw new BattleGameException("Game over. Player " + winningColor + " wins!");
    }

    private CommandDTO generateRandomMove(Long gameId, Long unitId) {
        Random random = new Random();
        Direction direction = Direction.values()[random.nextInt(Direction.values().length)];
        int verticalSteps = random.nextInt(3) + 1;
        int horizontalSteps = random.nextInt(3) + 1;

        CommandDTO commandDTO = new CommandDTO();
        commandDTO.setGameId(gameId);
        commandDTO.setUnitId(unitId);
        commandDTO.setDirection(direction);
        commandDTO.setVerticalSteps(verticalSteps);
        commandDTO.setHorizontalSteps(horizontalSteps);
        return commandDTO;
    }

    private boolean cannotExecuteCommand(LocalDateTime lastCommand, long requiredInterval) {
        if (lastCommand == null) {
            return false;
        }
        long secondsSinceLastCommand = Duration.between(lastCommand, LocalDateTime.now()).getSeconds();
        return secondsSinceLastCommand < requiredInterval;
    }

    private boolean isPositionValid(Position position, int width, int height) {
        return position.getX() >= 0 && position.getX() < width && position.getY() >= 0 && position.getY() < height;
    }

    private Position calculateNewPosition(Position oldPosition, Direction direction, int verticalDistance, int horizontalDistance) {
        return switch (direction) {
            case UP -> new Position(oldPosition.getX(), oldPosition.getY() - verticalDistance);
            case DOWN -> new Position(oldPosition.getX(), oldPosition.getY() + verticalDistance);
            case LEFT -> new Position(oldPosition.getX() - horizontalDistance, oldPosition.getY());
            case RIGHT -> new Position(oldPosition.getX() + horizontalDistance, oldPosition.getY());
            case UP_LEFT ->
                    new Position(oldPosition.getX() - horizontalDistance, oldPosition.getY() - verticalDistance);
            case UP_RIGHT ->
                    new Position(oldPosition.getX() + horizontalDistance, oldPosition.getY() - verticalDistance);
            case DOWN_LEFT ->
                    new Position(oldPosition.getX() - horizontalDistance, oldPosition.getY() + verticalDistance);
            case DOWN_RIGHT ->
                    new Position(oldPosition.getX() + horizontalDistance, oldPosition.getY() + verticalDistance);
        };
    }
}
