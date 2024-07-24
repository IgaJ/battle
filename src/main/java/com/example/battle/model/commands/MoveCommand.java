package com.example.battle.model.commands;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@RequiredArgsConstructor
@DiscriminatorValue("MOVE")
@EqualsAndHashCode(callSuper = true)
public class MoveCommand extends Command {
    private Direction direction;
    private int verticalSteps;
    private int horizontalSteps;
}
