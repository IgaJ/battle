package com.example.battle.repositories;

import com.example.battle.events.CommandEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandEventRepository extends JpaRepository<CommandEvent, Long> {
}
