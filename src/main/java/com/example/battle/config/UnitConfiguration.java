package com.example.battle.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "unit")
public class UnitConfiguration {
    private int archerCount;
    private int cannonCount;
    private int transportCount;
}
