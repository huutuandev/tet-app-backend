package com.tet.tet_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "refresh:";

    public void save(Long userId, String token, long days) {
        redisTemplate.opsForValue().set(PREFIX + token, userId.toString(), days, TimeUnit.DAYS);
    }

    public Long getUserId(String token) {
        String userIdStr = redisTemplate.opsForValue().get(PREFIX + token);
        return userIdStr != null ? Long.valueOf(userIdStr) : null;
    }

    public void delete(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}

