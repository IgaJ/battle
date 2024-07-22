package com.example.battle.services;

import com.example.battle.events.FireCommandEvent;
import com.example.battle.events.MoveCommandEvent;
import com.example.battle.model.*;
import com.example.battle.model.units.Archer;
import com.example.battle.model.units.Cannon;
import com.example.battle.model.units.Transport;
import com.example.battle.repositories.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final EventPublisher eventPublisher;

    @Value("${board.width}")
    private int width;

    @Value("${board.height}")
    private int height;

    @Value("${unit.archers}")
    private int archerCount;

    @Value("${unit.cannons}")
    private int cannonCount;

    @Value("${unit.transports}")
    private int transportCount;

    public void createNewGame() {
        Board board = new Board();
        board.setWidth(width);
        board.setHeight(height);
        board.setUnits(new ArrayList<>());

        List<Unit> units = new ArrayList<>();
        addUnits(units, "white", board, archerCount, cannonCount, transportCount);
        addUnits(units, "black", board, archerCount, cannonCount, transportCount);

        Game game = new Game();
        game.setBoard(board);
        game.setUnits(units.stream().collect(Collectors.toMap(Unit::getId, Function.identity())));
        game.setLastCommands(new HashMap<>());

        gameRepository.save(game);
    }

    public List<Unit> listUnits(String gameId, String player) {
        return null;
    }

    @Transactional
    public void moveCommand(MoveCommandEvent command) { // publikowanie zdarzeń do EventPublisher, EventPublisher publikuje zdarzenie na busie zdarzeń,
        // który zostanie obsłużony przez CommandEventListener. CommandEventListener nasłuchuje zdarzeń MoveCommandEvent i FireCommandEvent.
        // Kiedy takie zdarzenie zostanie opublikowane, odpowiednia metoda w CommandEventListener zostanie wywołana i przetworzy zdarzenie.
        eventPublisher.publishEvent(command);
    }

    @Transactional
    public void fireCommand(FireCommandEvent command) { // publikowanie zdarzeń do EventPublisher
        eventPublisher.publishEvent(command);
    }

    private void addUnits(List<Unit> units, String player, Board board, int archerCount, int cannonCount, int transportCount) {
        addUnitsOfType(units, player, board, archerCount, Archer.class);
        addUnitsOfType(units, player, board, cannonCount, Cannon.class);
        addUnitsOfType(units, player, board, transportCount, Transport.class);
    }

        private <T extends Unit> void addUnitsOfType (List<Unit> units, String player, Board board, int count, Class<T> unitType) {
            for (int i = 0; i < count; i++) {
                try {
                    T unit = unitType.getDeclaredConstructor().newInstance();
                    unit.setPlayer(player);
                    unit.setPosition(randomPosition(board.getWidth(), board.getHeight(), units));
                    unit.setUnitStatus(UnitStatus.ACTIVE);
                    unit.setMoveCount(0);
                    units.add(unit);
                    board.addUnit(unit, unit.getPosition().getX(), unit.getPosition().getY());
                } catch (Exception e) {
                    throw new RuntimeException("Error creating unit of type: " + unitType.getName(), e);
                }
            }
        }
            /*Unit archer = Archer.builder()
                    .player(player)
                    .position(randomPosition(board.getWidth(), board.getHeight(), units))
                    .unitStatus(UnitStatus.ACTIVE)
                    .moveCount(0)
                    .build();
            board.addUnit(archer, archer.getPosition().getX(), archer.getPosition().getY());
            units.add(archer);
        }
*/
/*        for (int i = 0; i < cannonCount; i++) {
            Unit cannon = Cannon.builder()
                    .player(player)
                    .position(randomPosition(board))
                    .unitStatus(UnitStatus.ACTIVE)
                    .moveCount(0)
                    .build();
            board.addUnit(cannon, cannon.getPosition().getX(), cannon.getPosition().getY());
            units.add(cannon);
        }*/

 /*       for (int i = 0; i < transportCount; i++) {
            Unit transport = Transport.builder()
                    .player(player)
                    .position(randomPosition(board))
                    .unitStatus(UnitStatus.ACTIVE)
                    .moveCount(0)
                    .build();
            board.addUnit(transport, transport.getPosition().getX(), transport.getPosition().getY());
            units.add(transport);
        }*/


    private Position randomPosition(int boardWidth, int boardHeight, List<Unit> existingUnits) {
        Random random = new Random();
        Position position;
        do {
            position = new Position(random.nextInt(boardWidth), random.nextInt(boardHeight));
        } while (isPositionOccupied(position, existingUnits));
        return position;
    }

    private boolean isPositionOccupied(Position position, List<Unit> existingUnits) {
        return existingUnits.stream().anyMatch(unit -> unit.getPosition().equals(position));
    }

}
