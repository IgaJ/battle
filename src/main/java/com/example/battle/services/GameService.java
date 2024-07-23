package com.example.battle.services;

import com.example.battle.events.FireCommandEvent;
import com.example.battle.events.MoveCommandEvent;
import com.example.battle.model.*;
import com.example.battle.model.units.Archer;
import com.example.battle.model.units.Cannon;
import com.example.battle.model.units.Transport;
import com.example.battle.repositories.GameRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    @PostConstruct
    public void createNewGame() {
        Board board = new Board();
        board.setWidth(width);
        board.setHeight(height);

        List<Unit> units = new ArrayList<>();
        addUnits(units, "white", archerCount, cannonCount, transportCount);
        addUnits(units, "black", archerCount, cannonCount, transportCount);
        board.setUnits(units);

        Game game = new Game();
        game.setBoard(board);
        game.setUnits(units);
        game.setCommands(new ArrayList<>());

        gameRepository.save(game);
    }

    @Transactional
    public void moveCommand(MoveCommandEvent command) {
        eventPublisher.publishEvent(command);
    }

    @Transactional
    public void fireCommand(FireCommandEvent command) {
        eventPublisher.publishEvent(command);
    }

    public List<UnitDTO> findAll() {
        List<Unit> units = gameRepository.findAllUnits();
        return units.stream()
                .map(UnitDTO::mapToDTO)
                .collect(Collectors.toList());
    }

    private void addUnits(List<Unit> units, String player, int archerCount, int cannonCount, int transportCount) {
        addUnitsOfType(units, player, archerCount, Archer.class);
        addUnitsOfType(units, player, cannonCount, Cannon.class);
        addUnitsOfType(units, player, transportCount, Transport.class);
    }

    private <T extends Unit> void addUnitsOfType(List<Unit> units, String player, int count, Class<T> unitType) {
        for (int i = 0; i < count; i++) {
            try {
                T unit = unitType.getDeclaredConstructor().newInstance();
                unit.setPlayerColor(player);
                unit.setPosition(randomPosition(width, height, units));
                unit.setUnitStatus(UnitStatus.ACTIVE);
                unit.setMoveCount(0);
                units.add(unit);

            } catch (Exception e) {
                throw new RuntimeException("Error creating unit of type: " + unitType.getName(), e);
            }
        }
    }

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
