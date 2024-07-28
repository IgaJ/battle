package com.example.battle.controllers;

import com.example.battle.model.GameDTO;
import com.example.battle.model.commands.CommandDTO;
import com.example.battle.services.CommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/command")
public class CommandController {

    private final CommandService commandService;

    @PostMapping("/move")
    public ResponseEntity<GameDTO> moveUnit(@RequestParam String color, @RequestBody CommandDTO commandDTO) {
        return ResponseEntity.ok(commandService.move(color, commandDTO));
    }

    @PostMapping("/fire")
    public ResponseEntity<GameDTO> fire(@RequestParam String color, @RequestBody CommandDTO commandDTO) {
        return ResponseEntity.ok(commandService.fire(color, commandDTO));
    }

    @PostMapping("/random") // to be completed
    public ResponseEntity<GameDTO> randomMove(@RequestParam String color, @RequestBody CommandDTO commandDTO) {
        return ResponseEntity.ok(commandService.randomMove(color, commandDTO));
    }
}
