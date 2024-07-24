package com.example.battle.repositories;

import com.example.battle.model.Game;
import com.example.battle.model.units.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT u FROM Game g JOIN g.units u")
    List<Unit> findAllUnits();
    Optional<Game> findByActiveTrue();
}