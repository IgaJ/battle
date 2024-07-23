package com.example.battle.events;

import com.example.battle.model.Game;
import com.example.battle.model.Position;
import com.example.battle.model.Unit;
import com.example.battle.model.UnitStatus;
import com.example.battle.repositories.GameRepository;
import com.example.battle.repositories.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommandEventListener {

    private final GameRepository gameRepository;
    private final UnitRepository unitRepository;

    @EventListener
    @Transactional
    public void handleMoveCommand(MoveCommandEvent event) {
        Optional<Game> gameOptional = gameRepository.findById(Long.parseLong(event.getGameId()));
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            Unit unit = game.getUnits().get(event.getUnitId());
            if (unit != null && unit.getUnitStatus() == UnitStatus.ACTIVE) {
                if (cannotExecuteCommand(event.getLastCommand(), unit.getRequiredInterval("move"))) {
                    throw new RuntimeException("Too early to execute command. Wait");
                }
                if (!unit.getPlayerColor().equals(event.getPlayerColor())) {
                    throw new RuntimeException("Incorrect player color");
                }
                Position newPosition = calculateNewPosition(
                        unit.getPosition(),
                        event.getDirection(),
                        event.getVerticalSteps(),
                        event.getHorizontalSteps()
                );

                if (isPositionValid(newPosition, game.getBoard().getWidth(), game.getBoard().getHeight())) {
                    Optional<Unit> targetUnitOptional = game.getBoard().getUnitPosition(newPosition.getX(), newPosition.getY());
                    if (targetUnitOptional.isPresent()) {
                        Unit targetUnit = targetUnitOptional.get();
                        if (!targetUnit.getPlayerColor().equals(unit.getPlayerColor())) {
                            targetUnit.setUnitStatus(UnitStatus.DESTROYED);
                            unitRepository.save(targetUnit);
                        } else {
                            throw new RuntimeException("The vehicle cannot invade its own unit");
                        }
                    } else {
                        unit.setPosition(newPosition);
                        unit.setMoveCount(unit.getMoveCount() + 1);
                    }

                    event.setLastCommand(LocalDateTime.now());
                    game.getCommands().add(event);
                    unitRepository.save(unit);
                } else {
                    throw new RuntimeException("Incorrect new position");
                }
            }
        }
    }

    @EventListener
    @Transactional
    public void handleFireCommand(FireCommandEvent event) {
        Optional<Game> gameOptional = gameRepository.findById(Long.parseLong(event.getGameId()));
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            Unit unit = game.getUnits().get(event.getUnitId());

            if (unit != null && unit.getUnitStatus() == UnitStatus.ACTIVE) {
                if (cannotExecuteCommand(event.getLastCommand(), unit.getRequiredInterval("fire"))) {
                    throw new RuntimeException("Too early to execute command. Wait");
                }

                if(!unit.getPlayerColor().equals(event.getPlayerColor())) {
                    throw new RuntimeException("Incorrect player color");
                }

                Position endPosition = calculateNewPosition(
                        unit.getPosition(),
                        event.getDirection(),
                        event.getVerticalDistance(),
                        event.getHorizontalDistance()
                );
                Optional<Unit> targetUnitOptional = game.getBoard().getUnitPosition(endPosition.getX(), endPosition.getY());

                if (targetUnitOptional.isPresent()) {
                    Unit targetUnit = targetUnitOptional.get();
                    targetUnit.setUnitStatus(UnitStatus.DESTROYED);
                    unitRepository.save(targetUnit);
                }
                event.setLastCommand(LocalDateTime.now());
                game.getCommands().add(event);
                unitRepository.save(unit);
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
