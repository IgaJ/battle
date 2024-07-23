package com.example.battle.controllers;

import com.example.battle.events.FireCommandEvent;
import com.example.battle.events.MoveCommandEvent;
import com.example.battle.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/vi//command")
public class CommandController {

    private final GameService gameService;

    @PostMapping("/move")
    public ResponseEntity<String> moveUnit(@RequestParam String playerColor, @RequestBody MoveCommandEvent command) {
        command.setPlayerColor(playerColor);
        gameService.moveCommand(command);
        return ResponseEntity.ok("Move command processed");
    }

    @PostMapping("/fire")
    public ResponseEntity<String> fire(@RequestParam String playerColor, @RequestBody FireCommandEvent command) {
        command.setPlayerColor(playerColor);
        gameService.fireCommand(command);
        return ResponseEntity.ok("Fire command processed");
    }

    @PostMapping("/random")
    public ResponseEntity<String> randomMove(@RequestParam String playerColor) {
        // to be completed
        return null;
    }
}
