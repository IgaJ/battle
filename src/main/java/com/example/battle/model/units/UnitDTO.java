package com.example.battle.model.units;

import com.example.battle.model.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitDTO {
    private Long id;
    private UnitType unitType;
    private String color;
    private Position position;
    private UnitStatus unitStatus;
    private int moveCount;

    public static UnitDTO mapToDTO(Unit unit) {
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setId(unit.getId());
        unitDTO.setUnitType(unit.getUnitType());
        unitDTO.setColor(unit.getColor());
        unitDTO.setPosition(unit.getPosition());
        unitDTO.setUnitStatus(unit.getUnitStatus());
        unitDTO.setMoveCount(unit.getMoveCount());
        return unitDTO;
    }
}
