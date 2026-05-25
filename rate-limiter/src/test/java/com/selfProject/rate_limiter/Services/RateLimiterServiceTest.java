package com.selfProject.rate_limiter.Services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class RateLimiterServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private DefaultRedisScript<Long> script;

    @Mock
    private RateLimiterMetrics metrics;

    @InjectMocks
    private RateLimiterService service;

    @BeforeEach
    void resetTemplate() {
        // no-op; each test stubs its own return value
    }

    @Test
    void isAllowed_returnsTrue_andIncrementsAllowed_whenScriptReturnsOne() {
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(),
                any(Object[].class))).thenReturn(1L);

        boolean allowed = service.isAllowed("alice", 5, 10_000);

        assertThat(allowed).isTrue();
        verify(metrics).incrementAllowed();
        verify(metrics, never()).incrementBlocked();
    }

    @Test
    void isAllowed_returnsFalse_andIncrementsBlocked_whenScriptReturnsZero() {
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(),
                any(Object[].class))).thenReturn(0L);

        boolean allowed = service.isAllowed("alice", 5, 10_000);

        assertThat(allowed).isFalse();
        verify(metrics).incrementBlocked();
        verify(metrics, never()).incrementAllowed();
    }

    @Test
    void isAllowed_returnsFalse_whenScriptReturnsNull() {
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(),
                any(Object[].class))).thenReturn(null);

        boolean allowed = service.isAllowed("alice", 5, 10_000);

        assertThat(allowed).isFalse();
        verify(metrics).incrementBlocked();
    }
}
