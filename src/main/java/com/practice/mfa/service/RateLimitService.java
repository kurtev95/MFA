package com.practice.mfa.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RateLimitService {
    private static final int RATE_LIMIT_THRESHOLD = 5;
    private static final Duration RATE_LIMIT_WINDOW = Duration.ofMinutes(1);

    private final RedisTemplate<String, String> redisTemplate;

    public RateLimitService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void handleRateLimit(String email, String rateLimitPrefix) throws TooManyRequestsException {
        String rateLimitKey = rateLimitPrefix + email;

        Long attemptCount = redisTemplate.opsForValue().increment(rateLimitKey);

        if (attemptCount == 1) {
            // Set TTL for the rate limit key on first verification attempt
            redisTemplate.expire(rateLimitKey, RATE_LIMIT_WINDOW);
        }

        if (attemptCount != null && attemptCount > RATE_LIMIT_THRESHOLD) {
            throw new TooManyRequestsException("Too many verification attempts. Please try again later.");
        }
    }

    public static class TooManyRequestsException extends Exception {
        public TooManyRequestsException(String message) {
            super(message);
        }
    }
}
