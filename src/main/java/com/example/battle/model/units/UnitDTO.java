package com.example.battle.model.units;

import com.example.battle.model.Position;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnitDTO {
    private Long id;
    private UnitType unitType;
    private String color;
    private Position position;
    private UnitStatus unitStatus;
    private int moveCount;

}
