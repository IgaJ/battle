package com.example.battle.model;

import com.example.battle.model.commands.Command;
import com.example.battle.model.units.Unit;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    private Board board;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Unit> units;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Command> commandHistory;
    private boolean active;
}
