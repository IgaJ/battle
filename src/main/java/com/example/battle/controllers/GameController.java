package com.example.battle.controllers;

import com.example.battle.model.UnitDTO;
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

    @PostMapping("/new")
    public ResponseEntity<String> newGame() {
        gameService.createNewGame();
        return ResponseEntity.ok("New game created");
    }

    @GetMapping("/units")
    public ResponseEntity<List<UnitDTO>> listUnits() {
        return ResponseEntity.ok(gameService.findAll());
    }
}
