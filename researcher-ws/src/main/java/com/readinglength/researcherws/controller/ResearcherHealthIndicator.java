package com.readinglength.researcherws.controller;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ResearcherHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up().withDetail("all gucci", true).build();
    }
}
