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
@DiscriminatorValue("CANNON")
@EqualsAndHashCode(callSuper = true)
public class Cannon extends Unit {
    public Cannon(Long id, UnitType unitType, String color, Position position, UnitStatus unitStatus, int moveCount) {
        super(id, unitType, color, position, unitStatus, moveCount);
    }

    public Cannon() {
    }

    @Override
    public long checkIfUnitCanExecuteCommand(String commandType) {
        if (commandType.equals("fire")){
            return 13;
        } else {
            throw new BattleGameException("Incorrect command for cannon");
        }
    }

    @Override
    public boolean isCorrectMoveRange(int verticalSteps, int horizontalSteps) {
        return false;
    }

    @Override
    public boolean isCorrectFireRange(int verticalSteps, int horizontalSteps) {
        return true;
    }
}
