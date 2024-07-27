package com.example.battle.mapers;

import com.example.battle.model.Game;
import com.example.battle.model.GameDTO;
import org.mapstruct.Mapper;

@Mapper
public interface GameMapper {
    Game map(GameDTO gameDTO);
    GameDTO map(Game game);
}
