package com.example.battle.model;

import com.example.battle.model.units.Archer;
import com.example.battle.model.units.Cannon;
import com.example.battle.model.units.Transport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitDTO {
    private Long id;
    private String player;
    private String type;
    private Position position;
    private UnitStatus unitStatus;
    private int moveCount;

    public static UnitDTO mapToDTO(Unit unit) {
        UnitDTO unitDTO = new UnitDTO();
        unitDTO.setId(unit.getId());
        unitDTO.setPlayer(unit.getPlayerColor());
        unitDTO.setPosition(unit.getPosition());
        unitDTO.setUnitStatus(unit.getUnitStatus());
        unitDTO.setMoveCount(unit.getMoveCount());
        String type;
        if (unit instanceof Archer) {
            type = "ARCHER";
        } else if (unit instanceof Cannon) {
            type = "CANNON";
        } else if (unit instanceof Transport) {
            type = "TRANSPORT";
        } else {
            throw new RuntimeException("Unit type unknown");
        }
        unitDTO.setType(type);
        return unitDTO;
    }
}
