package com.selfProject.rate_limiter.Controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.selfProject.rate_limiter.Config.RateLimiterProperties;
import com.selfProject.rate_limiter.Services.RateLimiterService;

@WebMvcTest(ApiController.class)
@Import(RateLimiterProperties.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RateLimiterService service;

    @Test
    void returns200_whenServiceAllows() throws Exception {
        when(service.isAllowed(eq("alice"), anyInt(), anyInt())).thenReturn(true);

        mockMvc.perform(get("/api/data").param("userId", "alice"))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));
    }

    @Test
    void returns429_withRetryAfter_whenServiceBlocks() throws Exception {
        when(service.isAllowed(eq("alice"), anyInt(), anyInt())).thenReturn(false);

        mockMvc.perform(get("/api/data").param("userId", "alice"))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().string("Too Many Requests"))
                .andExpect(header().string("Retry-After", "10"));
    }
}
