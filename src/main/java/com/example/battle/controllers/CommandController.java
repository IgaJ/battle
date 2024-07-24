package com.example.battle.controllers;

import com.example.battle.model.commands.FireCommand;
import com.example.battle.model.commands.MoveCommand;
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
    public ResponseEntity<String> moveUnit(@RequestParam String color, @RequestBody MoveCommand command) {
        command.setColor(color);
        commandService.move(command);
        return ResponseEntity.ok("Move command processed");
    }

    @PostMapping("/fire")
    public ResponseEntity<String> fire(@RequestParam String color, @RequestBody FireCommand command) {
        command.setColor(color);
        commandService.fire(command);
        return ResponseEntity.ok("Fire command processed");
    }

    @PostMapping("/random")
    public ResponseEntity<String> randomMove(@RequestParam String playerColor) {
        // to be completed
        return null;
    }
}
