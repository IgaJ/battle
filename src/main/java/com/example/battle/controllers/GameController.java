package com.example.battle.controllers;

import com.example.battle.model.Unit;
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
    public ResponseEntity<List<UnitDTO>> newGame() {
        return ResponseEntity.ok(gameService.createNewGame());
    }

    @GetMapping("/units")
    public ResponseEntity<List<UnitDTO>> listUnits() {
        return ResponseEntity.ok(gameService.findAll());
    }
}
