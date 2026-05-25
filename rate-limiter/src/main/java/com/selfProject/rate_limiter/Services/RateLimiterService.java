package com.selfProject.rate_limiter.Services;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private DefaultRedisScript<Long> script;


    @Autowired
    private RateLimiterMetrics metrics;

    public boolean isAllowed(String userId, int limit, int windowMs) {

        String key = "rate_limit:" + userId;

        Long result = redisTemplate.execute(
                script,
                Collections.singletonList(key),
                String.valueOf(System.currentTimeMillis()),
                String.valueOf(windowMs),
                String.valueOf(limit)
        );

        boolean allowed = result != null && result == 1L;
        if (allowed) {
            metrics.incrementAllowed();
        } else {
            metrics.incrementBlocked();
        }
        return allowed;
    }

    
}
