package com.example.battle.controllers;

import com.example.battle.events.FireCommandEvent;
import com.example.battle.events.MoveCommandEvent;
import com.example.battle.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/command")
public class CommandController {

    private final GameService gameService;

    @PostMapping("/move")
    public ResponseEntity<String> moveUnit(@RequestBody MoveCommandEvent command) {
        gameService.moveCommand(command);
        return ResponseEntity.ok("Move command done");
    }

    @PostMapping("/fire")
    public ResponseEntity<String> fire(@RequestBody FireCommandEvent command) {
        gameService.fireCommand(command);
        return ResponseEntity.ok("Fire command done");
    }
}
