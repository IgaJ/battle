package com.example.battle.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int width;
    private int height;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private List<Unit> units;

    public Optional<Unit> getUnitPosition(int x, int y) {
        return units.stream()
                .filter(unit -> unit.getPosition().getX() == x && unit.getPosition().getY() == y)
                .findFirst();
    }

    public void addUnit(Unit unit, int x, int y) {
        unit.setPosition(new Position(y, y));
        units.add(unit);
    }

    public void removeUnit(Unit unit) {
        units.remove(unit);
    }
}
