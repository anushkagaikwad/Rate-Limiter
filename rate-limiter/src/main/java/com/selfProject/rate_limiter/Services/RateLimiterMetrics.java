package com.selfProject.rate_limiter.Services;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class RateLimiterMetrics {

    private final Counter allowedRequests;
    private final Counter blockedRequests;

    public RateLimiterMetrics(MeterRegistry registry) {
        allowedRequests = registry.counter("rate_limiter_allowed");
        blockedRequests = registry.counter("rate_limiter_blocked");
    }

    public void incrementAllowed() {
        allowedRequests.increment();
    }

    public void incrementBlocked() {
        blockedRequests.increment();
    }
}
