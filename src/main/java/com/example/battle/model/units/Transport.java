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
@DiscriminatorValue("TRANSPORT")
@EqualsAndHashCode(callSuper = true)
public class Transport extends Unit {
    public Transport(Long id, UnitType unitType, String color, Position position, UnitStatus unitStatus, int moveCount) {
        super(id, unitType, color, position, unitStatus, moveCount);
    }

    public Transport() {
    }

    @Override
    public long checkIfUnitCanExecuteCommand(String commandType) {
        if (commandType.equals("move")) {
            return 7;
        } else {
            throw new BattleGameException("Incorrect command for transport");
        }
    }

    @Override
    public boolean canMove(int verticalSteps, int horizontalSteps) {
        int absVertical = Math.abs(verticalSteps);
        int absHorizontal = Math.abs(horizontalSteps);
        return (absVertical == 0 || absHorizontal == 0) && (absVertical <= 3 && absHorizontal <= 3);
    }
}
