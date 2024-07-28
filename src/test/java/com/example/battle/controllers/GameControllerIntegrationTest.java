package com.example.battle.controllers;

import com.example.battle.model.GameDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GameControllerIntegrationTest {

    @Autowired
    GameController gameController;

    @Rollback
    @Test
    void newGameShouldCreateGameWithSixUnits() {
        GameDTO gameDTO = gameController.newGame().getBody();
        assertEquals(6, gameDTO.getUnits().size());
    }

}
