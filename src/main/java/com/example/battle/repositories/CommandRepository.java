package com.example.battle.repositories;

import com.example.battle.model.commands.Command;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandRepository extends JpaRepository<Command, Long> {
}
