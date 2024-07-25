package com.example.battle.controllers;

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
    public ResponseEntity<Void> moveUnit(@RequestParam String color, @RequestBody CommandDTO commandDTO) {
        commandService.move(color, commandDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/fire")
    public ResponseEntity<Void> fire(@RequestParam String color, @RequestBody CommandDTO commandDTO) {
        commandService.fire(color, commandDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/random")
    public ResponseEntity<Void> randomMove(@RequestParam String color, @RequestBody CommandDTO commandDTO) {
        commandService.randomMove(color, commandDTO);
        return ResponseEntity.noContent().build();
    }
}
