package com.example.battle.services;

import com.example.battle.events.CommandEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisher {
    private final ApplicationEventPublisher publisher;

    public void pubslishEvent(CommandEvent event) {
        publisher.publishEvent(event);
    }
}
