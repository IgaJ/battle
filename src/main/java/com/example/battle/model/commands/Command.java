package com.example.battle.model.commands;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long gameId;
    private Long unitId;
    private LocalDateTime lastCommand;
    private Direction direction;
    private int verticalSteps;
    private int horizontalSteps;
}
