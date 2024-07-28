package com.example.battle.services;

import com.example.battle.config.BoardConfiguration;
import com.example.battle.config.WebSocketHandler;
import com.example.battle.exceptions.BattleGameException;
import com.example.battle.mapers.CommandMapper;
import com.example.battle.mapers.GameMapper;
import com.example.battle.model.Game;
import com.example.battle.model.GameDTO;
import com.example.battle.model.Position;
import com.example.battle.model.commands.Command;
import com.example.battle.model.commands.CommandDTO;
import com.example.battle.model.commands.Direction;
import com.example.battle.model.units.*;
import com.example.battle.repositories.GameRepository;
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
    private final GameService gameService;
    private final BoardConfiguration boardConfiguration;
    private final CommandMapper commandMapper;
    private final GameMapper gameMapper;
    private final WebSocketHandler webSocketHandler;

    @Transactional
    public GameDTO move(String color, CommandDTO commandDTO) {
        validateCommandSteps(commandDTO);

        Game game = gameRepository.findById(commandDTO.getGameId()).orElseThrow(() -> new BattleGameException("Can't find game of given Id"));
        if (!game.isActive()) {
            throw new BattleGameException("Selected game is not active");
        }
        Unit unit = getUnitFromDatabase(color, commandDTO, "move");
        Position newPosition = calculateNewPosition(
                unit.getPosition(),
                commandDTO.getDirection(),
                commandDTO.getVerticalSteps(),
                commandDTO.getHorizontalSteps()
        );
        Game gameAfterSave = null;
        if (!isPositionValid(newPosition, boardConfiguration.getWidth(), boardConfiguration.getHeight())) {
            throw new BattleGameException("Incorrect new position");
        }
        Optional<Unit> targetUnitOptional = game.getUnits().stream()
                .filter(found -> found.getPosition().equals(newPosition))
                .findFirst();

        Command command = commandMapper.map(commandDTO);
        command.setLastCommand(LocalDateTime.now());
        game.getCommandHistory().add(command);

        if (targetUnitOptional.isPresent()) {
            Unit targetUnit = targetUnitOptional.get();
            if(unit.getUnitType() == UnitType.ARCHER) {
                throw new BattleGameException("Archer unit cannot move to an occupied position");
            }
            if (targetUnit.getColor().equals(unit.getColor())) {
                throw new BattleGameException("The vehicle cannot invade its own unit");
            }

            targetUnit.setUnitStatus(UnitStatus.DESTROYED);
            unit.setPosition(newPosition);
            game.getUnits().remove(targetUnit);

            if (game.getUnits().stream().noneMatch(u -> u.getColor().equals(targetUnit.getColor()) &&
                    u.getUnitStatus() == UnitStatus.ACTIVE)) {
                endGame(targetUnit.getColor(), game);
            }
            gameAfterSave = gameRepository.save(game);
            gameService.printBoard(gameAfterSave.getUnits());
            return gameMapper.map(gameAfterSave);

        } else {
            unit.setPosition(newPosition);
            unit.setMoveCount(unit.getMoveCount() + 1);
        }

        gameAfterSave = gameRepository.save(game);
        gameService.printBoard(gameAfterSave.getUnits());

        try {
            webSocketHandler.broadcast("Unit moved: " + commandDTO.getUnitId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        // should save!
        return gameMapper.map(gameAfterSave);
        //return gameMapper.map(gameRepository.findById(commandDTO.getGameId()).orElseThrow(() -> new BattleGameException("Game not found with id " + commandDTO.getGameId())));
    }

    @Transactional
    public GameDTO fire(String color, CommandDTO commandDTO) {
        validateCommandSteps(commandDTO);

        Optional<Game> gameOptional = gameRepository.findById(commandDTO.getGameId());
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            Unit unit = getUnitFromDatabase(color, commandDTO, "fire");

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
                if (game.getUnits().stream().noneMatch(u -> u.getColor().equals(targetUnit.getColor()) && u.getUnitStatus() == UnitStatus.ACTIVE)) {
                    endGame(targetUnit.getColor(), game);
                }

            }
            Command command = commandMapper.map(commandDTO);
            command.setLastCommand(LocalDateTime.now());
            game.getCommandHistory().add(command);
            gameService.printBoard();
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

            if (unit != null && unit.getUnitStatus() == UnitStatus.ACTIVE) {
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

    private void validateCommandSteps(CommandDTO commandDTO) {
        Direction direction = commandDTO.getDirection();
        int verticalSteps = commandDTO.getVerticalSteps();
        int horizontalSteps = commandDTO.getHorizontalSteps();

        if ((direction == Direction.RIGHT || direction == Direction.LEFT) && verticalSteps != 0) {
            throw new BattleGameException("Vertical steps should be 0 when direction is RIGHT or LEFT");
        }
        if ((direction == Direction.UP || direction == Direction.DOWN) && horizontalSteps != 0) {
            throw new BattleGameException("Horizontal steps should be 0 when direction is UP or DOWN");
        }
    }

    private Unit getUnitFromDatabase(String color, CommandDTO commandDTO, String commandType) {
        Optional<Game> gameOptional = gameRepository.findById(commandDTO.getGameId());
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            Unit unit = game.getUnits().stream().filter(u -> Objects.equals(u.getId(), commandDTO.getUnitId())).findFirst().orElse(null);

            if (unit != null && unit.getUnitStatus() == UnitStatus.ACTIVE) {
                if (cannotExecuteCommand(commandDTO.getLastCommand(), unit.checkIfUnitCanExecuteCommand(commandType))) {
                    throw new BattleGameException("Too early to execute command. Wait");
                }
                if (!unit.getColor().equals(color)) {
                    throw new BattleGameException("Incorrect player color");
                }
                return unit;
            }
        }
        throw new BattleGameException("Can't find valid unit of given Id");
    }

    private void endGame(String losingColor, Game game) {
        String winningColor = losingColor.equals("white") ? "black" : "white";
        game.setActive(false);
        Game gameSaved = gameRepository.save(game);
        gameService.printBoard(gameSaved.getUnits());
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
