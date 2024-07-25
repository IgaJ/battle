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
    private UnitStatus unitStatus;
    private int moveCount;

//id, kierunek, ilość kroków

    public static UnitDTO mapToDTO(Unit unit) {
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setId(unit.getId());
        unitDTO.setUnitType(unit.getUnitType());
        unitDTO.setColor(unit.getColor());
        unitDTO.setUnitStatus(unit.getUnitStatus());
        unitDTO.setMoveCount(unit.getMoveCount());
        return unitDTO;
    }
}
