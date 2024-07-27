package com.example.battle.controllers;

import com.example.battle.exceptions.BattleGameException;
import com.example.battle.model.GameDTO;
import com.example.battle.model.commands.CommandDTO;
import com.example.battle.model.commands.Direction;
import com.example.battle.services.CommandService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommandController.class)
public class CommandControllerTest {


    @Autowired
    MockMvc mockMvc;
    @MockBean
    CommandService commandService;
    ObjectMapper objectMapper = new ObjectMapper();
    CommandDTO commandDTO;
    String requestBody;

    @BeforeEach
    void prepare() throws JsonProcessingException {
        commandDTO = new CommandDTO(null, 1L, 4L, null, null, Direction.DOWN, 1, 0);
        requestBody = objectMapper.writeValueAsString(commandDTO);
    }

    @Test
    void shouldReturnOkAndJsonFromCommandDtoWhenGetsProperRequestToMove() throws Exception {
        when(commandService.move(anyString(), any(CommandDTO.class))).thenReturn(new GameDTO(1L, Collections.emptyList(), Collections.emptyList(), true));
        mockMvc.perform(post("/api/v1/command/move")
                        .param("color", "black")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void shouldReturnBadRequestAndWhenGetsNotProperRequestToMove() throws Exception {
        when(commandService.move(anyString(), any(CommandDTO.class))).thenThrow(new BattleGameException("Some improper request"));
        mockMvc.perform(post("/api/v1/command/move")
                        .param("color", "black")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Some improper request"));
    }

    @Test
    void shouldReturnOkAndJsonFromCommandDtoWhenGetsProperRequestToFire() throws Exception {
        when(commandService.fire(anyString(), any(CommandDTO.class))).thenReturn(new GameDTO(1L, Collections.emptyList(), Collections.emptyList(), true));
        mockMvc.perform(post("/api/v1/command/fire")
                        .param("color", "black")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void shouldReturnBadRequestAndWhenGetsNotProperRequestToFire() throws Exception {
        when(commandService.fire(anyString(), any(CommandDTO.class))).thenThrow(new BattleGameException("Some improper request"));
        mockMvc.perform(post("/api/v1/command/fire")
                        .param("color", "black")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Some improper request"));
    }

}
