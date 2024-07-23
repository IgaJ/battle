package com.example.battle.repositories;

import com.example.battle.model.Game;
import com.example.battle.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT u FROM Unit u WHERE u.playerColor = :playerColor")
    List<Unit> findAllUnitsByPlayerColor(@Param("playerColor") String playerColor);
}
