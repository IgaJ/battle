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

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommandEventListener {

    private final GameRepository gameRepository;
    private final UnitRepository unitRepository;

    @EventListener // Spring Event Bus jest implekentowany automatycznie i działa na podst. mechanizmu zdarzeń w Springu.
    // Używanie @EventListener i ApplicationEventPublisher umożliwia korzystanie z tego mechanizmu:
    // 1. Spring zapewnia ApplicationEventPublisher, który jest odpowiedzialny za publikowanie zdarzeń. Wstrzykujesz ApplicationEventPublisher do dowolnego
    // beana (tu do GameService) i używasz go do publikowania zdarzeń.
    // 2. @EventListener oznacza, że metoda jest wywołana gdy zdarzenie określone przez typ parametru metody zostanie opublikowane. Spring automatycznie
    // wykrywa te metody i rejestruje je jako listenerów dla odpowiednich zdarzeń.
    // 3. Kiedy wywołam publishEvent w GameService, Spring Event Bus przekazuje to zdarzenie wszystkich zarejestrowanych listenerów, zainteresowanych tym zdarzeniem.
    @Transactional
    public void handleMoveCommand(MoveCommandEvent event){
        // pobierz grę z BD
        Optional<Game> gameOptional = gameRepository.findById(Long.parseLong(event.getGameId()));
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            // pobierz jednostkę z mapy jednostek którą posiada gra
            Unit unit = game.getUnits().get(event.getUnitId());
            if (unit != null && unit.getUnitStatus() == UnitStatus.ACTIVE) {
                // ustal nową pozycję jednostki
                Position oldPosition = unit.getPosition();
                Position newPosition = calculateNewPosition(oldPosition, event.getDirection(), event.getSteps());

                // spr czy nowa pozycja jest poprawna i wolna
                if (isPositionValid(newPosition, game.getBoard().getWidth(), game.getBoard().getHeight()) 
                        && game.getBoard().getUnitPosition(newPosition.getX(), newPosition.getY()).isEmpty()) {
                    unit.setPosition(newPosition);
                    unit.setMoveCount(unit.getMoveCount() + 1);

                    // aktualizuj ostatni ruch jednostki
                    game.getLastCommands().put(event.getUnitId(), event.getTimestamp());
                    unitRepository.save(unit);
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
                // oblicz nową pozycję celu
                Position targetPosition =calculateNewPosition(unit.getPosition(), event.getDirection(), event.getDistance());
                Optional<Unit> targetUnitOptional = game.getBoard().getUnitPosition(targetPosition.getX(), targetPosition.getY());
                if (targetUnitOptional.isPresent()) {
                    Unit targetUnit = targetUnitOptional.get();
                    targetUnit.setUnitStatus(UnitStatus.DESTROYED);
                    unitRepository.save(targetUnit);
                }
                game.getLastCommands().put(event.getUnitId(), event.getTimestamp());
            }
        }
    }

    private boolean isPositionValid(Position position, int width, int height) {
        return position.getX() >= 0 && position.getX() < width && position.getY() >= 0 && position.getY() < height;
    }

    private Position calculateNewPosition(Position oldPosition, Direction direction, int distance) {
        return switch (direction) {
            case UP -> new Position(oldPosition.getX(), oldPosition.getY() - distance);
            case DOWN -> new Position(oldPosition.getX(), oldPosition.getY() + distance);
            case LEFT -> new Position(oldPosition.getX() - distance, oldPosition.getY());
            case RIGHT -> new Position(oldPosition.getX() + distance, oldPosition.getY());
        };
    }
}
