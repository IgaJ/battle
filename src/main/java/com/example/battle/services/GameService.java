package com.example.battle.services;

import com.example.battle.model.*;
import com.example.battle.model.units.*;
import com.example.battle.repositories.GameRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

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
    public List<UnitDTO> createNewGame() {
        List<Game> games = gameRepository.findAll();
        for (Game game : games) {
            game.setActive(false);
        }

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
        game.setCommandHistory(new ArrayList<>());
        game.setActive(true);
        gameRepository.save(game);

        printBoard(board);

        return units.stream().map(UnitDTO::mapToDTO).collect(Collectors.toList());
    }

    private void printBoard(Board board) {
        Map<Position, Unit> unitPositionMap = new HashMap<>();

        // mapowane jednostki na pozycje
        for (Unit unit : board.getUnits()) {
            Position pos = unit.getPosition();
            unitPositionMap.put(pos, unit);
        }

        int columnWidth = 10;

        // numery kolumn
        System.out.print(" ");
        for (int x = 0; x < width; x++) {
            System.out.printf("%" + columnWidth + "d", x);
        }
        System.out.println();

        // wiersze
        for (int y = 0; y < height; y++) {
            System.out.printf("%2d ", y);
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                if (unitPositionMap.containsKey(pos)) {
                    Unit unit = unitPositionMap.get(pos);
                    String unitType = unit.getUnitType().name().substring(0, 1);  // Pierwsza litera typu jednostki
                    String unitColor = unit.getColor().substring(0, 1);  // Pierwsza litera koloru
                    String unitId = unit.getId().toString();  // ID jednostki

                    String unitRepresentation = unitType + "/" + unitColor + "/" + unitId;
                    System.out.printf("%-" + columnWidth + "s", unitRepresentation);
                } else {
                    System.out.printf("%-" + columnWidth + "s", "X");
                }
            }
            System.out.println();
        }
    }

    private void addUnits(List<Unit> units, String color, int archerCount, int cannonCount, int transportCount) {
        for (int i = 0; i < archerCount; i++) {
            Unit unit = new Archer();
            unit.setUnitType(UnitType.ARCHER);
            unit.setColor(color);
            unit.setPosition(randomPosition(width, height, units));
            unit.setUnitStatus(UnitStatus.ACTIVE);
            unit.setMoveCount(0);
            units.add(unit);
        }

        for (int i = 0; i < cannonCount; i++) {
            Unit unit = new Cannon();
            unit.setUnitType(UnitType.CANNON);
            unit.setColor(color);
            unit.setPosition(randomPosition(width, height, units));
            unit.setUnitStatus(UnitStatus.ACTIVE);
            unit.setMoveCount(0);
            units.add(unit);
        }

        for (int i = 0; i < transportCount; i++) {
            Unit unit = new Transport();
            unit.setUnitType(UnitType.TRANSPORT);
            unit.setColor(color);
            unit.setPosition(randomPosition(width, height, units));
            unit.setUnitStatus(UnitStatus.ACTIVE);
            unit.setMoveCount(0);
            units.add(unit);
        }
    }

    public List<UnitDTO> findAll() {
        List<Unit> units = gameRepository.findAllUnits();
        return units.stream()
                .map(UnitDTO::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<UnitDTO> findUnitsInActiveGame() {
        Optional<Game> game = gameRepository.findByActiveTrue();
        List<UnitDTO> units = new ArrayList<>();
        if (game.isPresent()) {
            units.addAll(game.get().getUnits().stream()
                    .map(UnitDTO::mapToDTO).toList());
        } else {
            throw new RuntimeException("No active game found");
        }
        return units;
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


    public void printBoard() {
        gameRepository.findByActiveTrue().ifPresent(game -> printBoard(game.getBoard()));
    }
}
