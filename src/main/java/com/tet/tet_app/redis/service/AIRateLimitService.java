package com.tet.tet_app.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AIRateLimitService {

    private final StringRedisTemplate redisTemplate;

    private static final int MAX_ASK_PER_DAY = 5;

    public void checkLimit(Long userId) {
        String date = LocalDate.now().toString();
        String key = "ai:ask:" + userId + ":" + date;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            // set TTL tới hết ngày
            redisTemplate.expireAt(
                    key,
                    LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
        }

        if (count != null && count > MAX_ASK_PER_DAY) {
            throw new RuntimeException("LIMIT_EXCEEDED");
        }
    }
}

