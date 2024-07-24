package com.example.battle.services;

import com.example.battle.model.Game;
import com.example.battle.model.Position;
import com.example.battle.model.commands.Direction;
import com.example.battle.model.commands.FireCommand;
import com.example.battle.model.commands.MoveCommand;
import com.example.battle.model.units.Unit;
import com.example.battle.model.units.UnitStatus;
import com.example.battle.repositories.GameRepository;
import com.example.battle.repositories.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommandService {

    private final GameRepository gameRepository;
    private final UnitRepository unitRepository;
    private final GameService gameService;

    @Transactional
    public void move(MoveCommand command) {
        Optional<Game> gameOptional = gameRepository.findById(command.getGameId());
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            List<Unit> units = game.getUnits();
            System.out.println("Lista unitów dla gry " + game.getId());
            for (Unit unit : units) {
                System.out.println(unit.getUnitType() + " " + unit.getColor() + " " + unit.getPosition());
            }
            //Unit unit = game.getUnits().get(command.getUnitId()-1);
            Unit unit = game.getUnits().stream().filter(u -> Objects.equals(u.getId(), command.getUnitId())).findFirst().orElse(null);
            System.out.println("Unit który ma się poruszać" + unit.getUnitType() + " " + unit.getColor());
            if (unit != null && unit.getUnitStatus() == UnitStatus.ACTIVE) {
                if (cannotExecuteCommand(command.getLastCommand(), unit.getRequiredInterval("move"))) {
                    throw new RuntimeException("Too early to execute command. Wait");
                }
                if (!unit.getColor().equals(command.getColor())) {
                    throw new RuntimeException("Incorrect player color");
                }
                Position newPosition = calculateNewPosition(
                        unit.getPosition(),
                        command.getDirection(),
                        command.getVerticalSteps(),
                        command.getHorizontalSteps()
                );

                if (isPositionValid(newPosition, game.getBoard().getWidth(), game.getBoard().getHeight())) {
                    Optional<Unit> targetUnitOptional = game.getBoard().getUnitPosition(newPosition.getX(), newPosition.getY());
                    if (targetUnitOptional.isPresent()) {
                        Unit targetUnit = targetUnitOptional.get();
                        System.out.println("Target: " + targetUnit.getUnitType() + " " + targetUnit.getColor());
                        if (!targetUnit.getColor().equals(unit.getColor())) {
                            targetUnit.setUnitStatus(UnitStatus.DESTROYED);
                            System.out.println("Cel zniszczony: " + targetUnit.getUnitType() + " " + targetUnit.getColor());
                            game.getUnits().remove(targetUnit);
                            List<Unit> units1 = game.getUnits();
                            System.out.println("Aktualna lista unitów dla gry " + game.getId());
                            for (Unit unit1 : units1) {
                                System.out.println(unit1.getUnitType() + " " + unit1.getColor() + " " + unit1.getPosition());
                            }
                            unitRepository.save(targetUnit);
                        } else {
                            throw new RuntimeException("The vehicle cannot invade its own unit");
                        }
                    } else {
                        unit.setPosition(newPosition);
                        System.out.println("Jednostka " + unit.getUnitType() + " " + unit.getColor() + "przesunięta na " + newPosition);
                        unit.setMoveCount(unit.getMoveCount() + 1);
                    }

                    command.setLastCommand(LocalDateTime.now());
                    game.getCommandHistory().add(command);
                    unitRepository.save(unit);
                    gameService.printBoard();
                } else {
                    throw new RuntimeException("Incorrect new position");
                }
            }
        }
    }

    @Transactional
    public void fire(FireCommand command) {
        Optional<Game> gameOptional = gameRepository.findById(command.getGameId());
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            //Unit unit = game.getUnits().get(command.getUnitId()-1);

            Unit unit = game.getUnits().stream().filter(u -> Objects.equals(u.getId(), command.getUnitId())).findFirst().orElse(null);
            if (unit != null && unit.getUnitStatus() == UnitStatus.ACTIVE) {
                if (cannotExecuteCommand(command.getLastCommand(), unit.getRequiredInterval("fire"))) {
                    throw new RuntimeException("Too early to execute command. Wait");
                }

                if(!unit.getColor().equals(command.getColor())) {
                    throw new RuntimeException("Incorrect player color");
                }

                Position endPosition = calculateNewPosition(
                        unit.getPosition(),
                        command.getDirection(),
                        command.getVerticalSteps(),
                        command.getHorizontalSteps()
                );
                Optional<Unit> targetUnitOptional = game.getBoard().getUnitPosition(endPosition.getX(), endPosition.getY());

                if (targetUnitOptional.isPresent()) {
                    Unit targetUnit = targetUnitOptional.get();
                    System.out.println("Target: " + targetUnit.getUnitType() + " " + targetUnit.getColor());
                    targetUnit.setUnitStatus(UnitStatus.DESTROYED);
                    System.out.println("Cel zniszczony: " + targetUnit.getUnitType() + " " + targetUnit.getColor());
                    game.getUnits().remove(targetUnit);
                    unitRepository.save(targetUnit);
                }
                command.setLastCommand(LocalDateTime.now());
                game.getCommandHistory().add(command);
                unitRepository.save(unit);
                gameService.printBoard();
            }
        }
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
