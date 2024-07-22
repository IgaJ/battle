package com.example.battle.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

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
    private Map<String, Unit> units;
    @ElementCollection
    private Map<String, LocalDateTime> lastCommands;

}
