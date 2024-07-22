package com.example.battle.services;

import com.example.battle.events.FireCommandEvent;
import com.example.battle.events.MoveCommandEvent;
import com.example.battle.model.Unit;
import com.example.battle.repositories.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final EventPublisher eventPublisher;

    public void createNewGame() {
    }

    public List<Unit> listUnits(String gameId, String player) {
        return null;
    }

    public void moveCommand(MoveCommandEvent command) {
    }

    public void fireCommand(FireCommandEvent command) {
    }
}
