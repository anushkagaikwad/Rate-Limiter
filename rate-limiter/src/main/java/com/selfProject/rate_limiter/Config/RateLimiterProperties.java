package com.selfProject.rate_limiter.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Positive;

/**
 * Binds the "rate-limiter.*" settings from application.yaml into a typed object.
 *
 * The values below act as defaults; anything present in application.yaml
 * (or env vars / command-line args) overrides them.
 *
 * {@code @Validated} + the constraints below make the app fail to start if a
 * bad value (zero or negative) is supplied, instead of silently misbehaving.
 */
@Component
@Validated
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterProperties {

    /** Max number of requests allowed inside the window. */
    @Positive
    private int limit = 5;

    /** Length of the sliding window, in milliseconds. */
    @Positive
    private int windowMs = 10000;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getWindowMs() {
        return windowMs;
    }

    public void setWindowMs(int windowMs) {
        this.windowMs = windowMs;
    }
}
