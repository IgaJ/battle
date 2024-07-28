package com.example.battle.controllers;

import com.example.battle.model.GameDTO;
import com.example.battle.model.units.UnitDTO;
import com.example.battle.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/game")
public class GameController {
    private final GameService gameService;

    @PostMapping("/new") // tworzy nową grę
    public ResponseEntity<GameDTO> newGame() {
        return ResponseEntity.ok(gameService.createNewGame());
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDTO> getGameById(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getGameById(gameId));
    }

    @GetMapping("/{gameId}/units")
    public ResponseEntity<List<UnitDTO>> getUnits(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getUnitsByGameId(gameId));
    }

    @GetMapping("/units") // for active game
    public ResponseEntity<List<UnitDTO>> getUnitsInActiveGame() {
        return ResponseEntity.ok(gameService.findUnitsInActiveGame());
    }

    @GetMapping("/board") // prints game board to console, admin helper method
    public void printBoardInActiveGame() {
       gameService.printBoard();
    }
}
