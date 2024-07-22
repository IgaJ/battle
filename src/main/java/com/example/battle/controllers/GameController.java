package com.example.battle.controllers;

import com.example.battle.model.Unit;
import com.example.battle.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    @PostMapping("/new")
    public ResponseEntity<String> newGame() {
        gameService.createNewGame();
        return ResponseEntity.ok("New game created");
    }

    @GetMapping("/units")
    public ResponseEntity<List<Unit>> listUnits(@RequestParam String gameId, @RequestParam String player) {
        List<Unit> units = gameService.listUnits(gameId, player);
        return ResponseEntity.ok(units);
    }
}
