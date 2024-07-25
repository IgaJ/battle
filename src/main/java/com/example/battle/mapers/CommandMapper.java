package com.example.battle.mapers;

import com.example.battle.model.commands.Command;
import com.example.battle.model.commands.CommandDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CommandMapper {
    Command map(CommandDTO commandDTO);
    CommandDTO map(Command command);
}
