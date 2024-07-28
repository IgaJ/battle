package com.example.battle.model.units;

import com.example.battle.exceptions.BattleGameException;
import com.example.battle.model.*;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@DiscriminatorValue("ARCHER")
@EqualsAndHashCode(callSuper = true)
public class Archer extends Unit {
    public Archer(Long id, UnitType unitType, String color, Position position, UnitStatus unitStatus, int moveCount) {
        super(id, unitType, color, position, unitStatus, moveCount);
    }

    public Archer() {
    }

    @Override
    public long checkIfUnitCanExecuteCommand(String commandType) {
        if (commandType.equals("move")) {
            return 5;
        } else if (commandType.equals("fire")){
            return 10;
        } else {
            throw new BattleGameException("Incorrect command for archer");
        }
    }

    @Override
    public boolean canMove(int verticalSteps, int horizontalSteps) {
        return Math.abs(verticalSteps) <= 1 && Math.abs(horizontalSteps) <= 1;
    }
}
