package com.selfProject.rate_limiter.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.selfProject.rate_limiter.Config.RateLimiterProperties;
import com.selfProject.rate_limiter.Services.RateLimiterService;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private RateLimiterService service;

    @Autowired
    private RateLimiterProperties properties;

    @GetMapping("/data")
    public ResponseEntity<String> getData(@RequestParam String userId) {

        boolean allowed = service.isAllowed(userId, properties.getLimit(), properties.getWindowMs());

        if (!allowed) {
            // Tell the client how long (in seconds) to wait before retrying.
            long retryAfterSeconds = (long) Math.ceil(properties.getWindowMs() / 1000.0);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds))
                    .body("Too Many Requests");
        }

        return ResponseEntity.ok("Success");
    }
}
