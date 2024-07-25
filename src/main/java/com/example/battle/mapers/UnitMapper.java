package com.example.battle.mapers;

import com.example.battle.model.units.Unit;
import com.example.battle.model.units.UnitDTO;
import org.mapstruct.Mapper;

@Mapper
public interface UnitMapper {
    UnitDTO map(Unit unit);
}
