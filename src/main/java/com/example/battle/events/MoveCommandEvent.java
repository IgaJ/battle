package com.example.battle.events;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@DiscriminatorValue("MOVE")
@EqualsAndHashCode(callSuper = true)
public class MoveCommandEvent extends CommandEvent{
    private Direction direction;
    private int steps;
}
