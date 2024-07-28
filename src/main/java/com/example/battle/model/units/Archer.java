package com.example.battle.model.units;

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
        } else {
            return 10;
        }
    }
}
