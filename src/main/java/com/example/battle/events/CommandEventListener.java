package com.example.battle.events;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CommandEventListener {
    @EventListener
    public void handleMoveCommand(MoveCommandEvent event){
        // logika przetwarzania ruchu
    }
    @EventListener
    public void handleFireCommand(FireCommandEvent event) {
        // logika przetwarzania strzału
    }
}
