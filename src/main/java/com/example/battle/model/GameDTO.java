package com.example.battle.model;

import com.example.battle.model.commands.Command;
import com.example.battle.model.units.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO {
    private Long id;
    private List<Unit> units;
    private List<Command> commandHistory;
    private boolean active;
}
