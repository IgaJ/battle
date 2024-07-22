package com.example.battle.model;

import jakarta.persistence.Embeddable;
import lombok.*;
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    private int x;
    private int y;
}
