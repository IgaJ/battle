package com.example.battle.model.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommandDTO {
    private Long id;
    private Long gameId;
    private Long unitId;
    private LocalDateTime lastCommand;
    private String color;
    private Direction direction;
    private int verticalSteps;
    private int horizontalSteps;
}
