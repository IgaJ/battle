package com.example.battle.model.units;

import com.example.battle.model.Position;
import com.example.battle.model.Unit;
import com.example.battle.model.UnitStatus;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@DiscriminatorValue("ARCHER")
@EqualsAndHashCode(callSuper = true)
public class Archer extends Unit {
    public Archer(Long id, String player, Position position, UnitStatus unitStatus, int moveCount) {
        super(id, player, position, unitStatus, moveCount);
    }

    public Archer() {
    }

    @Override
    public long getRequiredInterval(String commandType) {
        if(commandType.equals("move")) {
            return 5;
        } else if (commandType.equals("fire")) {
            return 10;
        } else  {
            throw new RuntimeException("Incorrect command for an archer");
        }
    }
}
