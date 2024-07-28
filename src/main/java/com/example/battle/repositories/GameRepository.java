package com.example.battle.repositories;

import com.example.battle.model.Game;
import com.example.battle.model.units.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByActiveTrue();
    @Query("SELECT g.units FROM Game g WHERE g.id = :gameId")
    List<Unit> findUnitsByGameId(@Param("gameId") Long gameId);
}