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
@DiscriminatorValue("TRANSPORT")
@EqualsAndHashCode(callSuper = true)
public class Transport extends Unit {
    public Transport(Long id, String player, Position position, UnitStatus unitStatus, int moveCount) {
        super(id, player, position, unitStatus, moveCount);
    }

    public Transport() {
    }

    @Override
    public long getRequiredInterval(String commandType) {
        if (commandType.equals("move")) {
            return 7;
        } else {
            throw new RuntimeException("Niewlaściwa komenda dla pojazdu");
        }
    }
}
