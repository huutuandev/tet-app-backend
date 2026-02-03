package com.tet.tet_app.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    public boolean isAllowed(String key, long ttlSeconds) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", ttlSeconds, TimeUnit.SECONDS);

        // true = chưa tồn tại → cho phép
        // false = đã tồn tại → bị rate limit
        return Boolean.TRUE.equals(success);
    }
}
