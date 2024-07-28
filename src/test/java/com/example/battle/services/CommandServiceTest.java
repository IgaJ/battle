package com.example.battle.services;

import com.example.battle.config.BoardConfiguration;
import com.example.battle.config.WebSocketHandler;
import com.example.battle.exceptions.BattleGameException;
import com.example.battle.mapers.CommandMapper;
import com.example.battle.mapers.CommandMapperImpl;
import com.example.battle.mapers.GameMapper;
import com.example.battle.mapers.GameMapperImpl;
import com.example.battle.model.Game;
import com.example.battle.model.GameDTO;
import com.example.battle.model.Position;
import com.example.battle.model.commands.CommandDTO;
import com.example.battle.model.commands.Direction;
import com.example.battle.model.units.*;
import com.example.battle.repositories.GameRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class CommandServiceTest {


    private static final long FIFTEEN_SECONDS = 15_000L;
    GameRepository gameRepository = Mockito.mock(GameRepository.class);
    GameService gameService = Mockito.mock(GameService.class);
    BoardConfiguration boardConfiguration = Mockito.mock(BoardConfiguration.class);
    CommandMapper commandMapper = new CommandMapperImpl();
    GameMapper gameMapper = new GameMapperImpl();
    CommandService commandService = new CommandService(gameRepository, gameService, boardConfiguration, commandMapper, gameMapper);


    @BeforeEach
    void prepare() {
        when(boardConfiguration.getWidth()).thenReturn(4);
        when(boardConfiguration.getHeight()).thenReturn(4);
    }

    @Test
    void shouldThrowExceptionWhenGetsNonExistingGameIdInParameter() {
        CommandDTO commandWithNotExistingGameID = new CommandDTO(null, 5000L, 4L, LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() - FIFTEEN_SECONDS), ZoneId.of("UTC")), Direction.UP, 1, 0);
        assertThrows(BattleGameException.class, () -> commandService.fire("white", commandWithNotExistingGameID));
    }

    @Test
    void moveShouldChangePositionOfUnit() {
        CommandDTO properCommand = new CommandDTO(null, 1L, 4L, LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() - FIFTEEN_SECONDS), ZoneId.of("UTC")), Direction.UP, 1, 0);
        List<Unit> units = new ArrayList<>();
        Archer archerWhite = new Archer(1L, UnitType.ARCHER, "white", new Position(0, 1), UnitStatus.ACTIVE, 0);
        Archer archerBlack = new Archer(4L, UnitType.ARCHER, "black", new Position(2, 2), UnitStatus.ACTIVE, 0);
        units.add(archerWhite);
        units.add(archerBlack);
        Game game = new Game(1L, units, new ArrayList<>(), true);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);
        List<Unit> unitsChanged = new ArrayList<>(List.of(new Archer(1L, UnitType.ARCHER, "white", new Position(0, 1), UnitStatus.ACTIVE, 0),
                new Archer(4L, UnitType.ARCHER, "black", new Position(2, 1), UnitStatus.ACTIVE, 1) ));
        Game gameSaved = new Game(1L, unitsChanged, new ArrayList<>(), true);
        when(gameRepository.save(any())).thenReturn(gameSaved);
        GameDTO gameDTO = commandService.move("black", properCommand);
        Mockito.verify(gameRepository).save(gameArgumentCaptor.capture());
        Game captured = gameArgumentCaptor.getValue();
        Unit unitCaptured = captured.getUnits().stream().filter(unit -> unit.getId()==4).findAny().get();
        assertEquals(new Position(2, 1), unitCaptured.getPosition());
        assertEquals(1, unitCaptured.getMoveCount());
    }


    @Test
    void moveShouldEliminateEnemyUnit() {
        CommandDTO properCommand = new CommandDTO(null, 1L, 1L, LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() - FIFTEEN_SECONDS), ZoneId.of("UTC")), Direction.RIGHT, 0, 1);
        List<Unit> units = new ArrayList<>();
        Transport transport = new Transport(1L, UnitType.TRANSPORT, "white", new Position(0, 1), UnitStatus.ACTIVE, 0);
        Archer archerBlack = new Archer(4L, UnitType.ARCHER, "black", new Position(2, 2), UnitStatus.ACTIVE, 0);
        Archer archerBlack2 = new Archer(5L, UnitType.ARCHER, "black", new Position(1, 1), UnitStatus.ACTIVE, 0);
        units.add(transport);
        units.add(archerBlack);
        units.add(archerBlack2);
        Game game = new Game(1L, units, new ArrayList<>(), true);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);
        List<Unit> unitsChanged = new ArrayList<>(List.of(new Archer(1L, UnitType.ARCHER, "white", new Position(1, 1), UnitStatus.ACTIVE, 1),
                new Archer(4L, UnitType.ARCHER, "black", new Position(2, 2), UnitStatus.ACTIVE, 0) ));
        Game gameSaved = new Game(1L, unitsChanged, new ArrayList<>(), true);
        when(gameRepository.save(any())).thenReturn(gameSaved);
        GameDTO gameDTO = commandService.move("white", properCommand);
        Mockito.verify(gameRepository).save(gameArgumentCaptor.capture());
        Game captured = gameArgumentCaptor.getValue();
        assertEquals(2, captured.getUnits().size());
    }

    @Test
    void moveShouldNotEliminateOwnUnit() {
        CommandDTO command = new CommandDTO(null, 1L, 5L, LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() - FIFTEEN_SECONDS), ZoneId.of("UTC")), Direction.DOWN, 1, 0);
        List<Unit> units = new ArrayList<>();
        Archer archerWhite = new Archer(1L, UnitType.ARCHER, "white", new Position(0, 1), UnitStatus.ACTIVE, 0);
        Archer archerBlack = new Archer(4L, UnitType.ARCHER, "black", new Position(2, 2), UnitStatus.ACTIVE, 0);
        Transport transportBlack = new Transport(5L, UnitType.TRANSPORT, "black", new Position(2, 1), UnitStatus.ACTIVE, 0);
        units.add(archerWhite);
        units.add(archerBlack);
        units.add(transportBlack);
        Game game = new Game(1L, units, new ArrayList<>(), true);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gameRepository.save(any())).thenReturn(game);
        Assertions.assertThrows(BattleGameException.class, () -> commandService.move("black", command),"The vehicle cannot invade its own unit");
    }

}
