package com.example.battle.model.units;

import com.example.battle.model.Position;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UnitType unitType;
    private String color;
    private Position position;
    private UnitStatus unitStatus;
    private int moveCount;

    public abstract long checkIfUnitCanExecuteCommand(String commandType);
}
