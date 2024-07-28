package com.example.battle.services;

import com.example.battle.config.BoardConfiguration;
import com.example.battle.config.UnitConfiguration;
import com.example.battle.mapers.GameMapper;
import com.example.battle.mapers.UnitMapper;
import com.example.battle.exceptions.BattleGameException;
import com.example.battle.model.Game;
import com.example.battle.model.GameDTO;
import com.example.battle.model.Position;
import com.example.battle.model.units.*;
import com.example.battle.repositories.GameRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final UnitConfiguration unitConfiguration;
    private final BoardConfiguration boardConfiguration;
    private final GameMapper gameMapper;
    private final UnitMapper unitMapper;
    @PostConstruct
    public GameDTO createNewGame() {
        List<Game> games = gameRepository.findAll();
        for (Game game : games) {
            game.setActive(false);
        }

        List<Unit> units = new ArrayList<>();
        setUnitPositions(units, "white", unitConfiguration.getArcherCount(), unitConfiguration.getCannonCount(), unitConfiguration.getTransportCount());
        setUnitPositions(units, "black", unitConfiguration.getArcherCount(), unitConfiguration.getCannonCount(), unitConfiguration.getTransportCount());

        Game game = new Game();
        game.setUnits(units);
        game.setCommandHistory(new ArrayList<>());
        game.setActive(true);
        Game savedGame = gameRepository.save(game);
        printBoard(units);

        return gameMapper.map(savedGame);
    }

    public void printBoard(List<Unit> units) {
        Map<Position, Unit> unitPositionMap = new HashMap<>();

        // mapowane jednostki na pozycje
        for (Unit unit : units) {
            Position pos = unit.getPosition();
            unitPositionMap.put(pos, unit);
        }

        int columnWidth = 10;
        System.out.print(" ");
        for (int x = 0; x < boardConfiguration.getWidth(); x++) {
            System.out.printf("%" + columnWidth + "d", x);
        }
        System.out.println();
        for (int y = 0; y < boardConfiguration.getHeight(); y++) {
            System.out.printf("%2d ", y);
            for (int x = 0; x < boardConfiguration.getWidth(); x++) {
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

    private void setUnitPositions(List<Unit> units, String color, int archerCount, int cannonCount, int transportCount) {
        for (int i = 0; i < archerCount; i++) {
            Unit unit = new Archer();
            unit.setUnitType(UnitType.ARCHER);
            unit.setColor(color);
            unit.setPosition(randomPosition(boardConfiguration.getWidth(), boardConfiguration.getHeight(), units));
            unit.setUnitStatus(UnitStatus.ACTIVE);
            unit.setMoveCount(0);
            units.add(unit);
        }

        for (int i = 0; i < cannonCount; i++) {
            Unit unit = new Cannon();
            unit.setUnitType(UnitType.CANNON);
            unit.setColor(color);
            unit.setPosition(randomPosition(boardConfiguration.getWidth(), boardConfiguration.getHeight(), units));
            unit.setUnitStatus(UnitStatus.ACTIVE);
            unit.setMoveCount(0);
            units.add(unit);
        }

        for (int i = 0; i < transportCount; i++) {
            Unit unit = new Transport();
            unit.setUnitType(UnitType.TRANSPORT);
            unit.setColor(color);
            unit.setPosition(randomPosition(boardConfiguration.getWidth(), boardConfiguration.getHeight(), units));
            unit.setUnitStatus(UnitStatus.ACTIVE);
            unit.setMoveCount(0);
            units.add(unit);
        }
    }

    public List<UnitDTO> findUnitsInActiveGame() {
        Optional<Game> game = gameRepository.findByActiveTrue();
        List<UnitDTO> units = new ArrayList<>();
        if (game.isPresent()) {
            units.addAll(game.get().getUnits().stream()
                    .map(unitMapper::map).toList());
        } else {
            throw new BattleGameException("No active game found");
        }
        return units;
    }

    public GameDTO getGameById(Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new BattleGameException("Can't find by gameId"));
        return gameMapper.map(game);
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
        gameRepository.findByActiveTrue().ifPresent(game -> printBoard(game.getUnits()));
    }

    public List<UnitDTO> getUnitsByGameId(Long gameId) {
        return (gameRepository.findUnitsByGameId(gameId)).stream().map(unitMapper::map).collect(Collectors.toList());
    }
}
