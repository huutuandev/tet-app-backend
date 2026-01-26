package com.tet.tet_app.redis.service;

import com.tet.tet_app.redis.model.TempUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TempUserService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PREFIX = "TEMP_USER:";

    public void saveTempUser(TempUser user, long minutes) {
        redisTemplate.opsForValue()
                .set(PREFIX + user.getEmail(), user, Duration.ofMinutes(minutes));
    }

    public TempUser getTempUser(String email) {
        return (TempUser) redisTemplate.opsForValue().get(PREFIX + email);
    }

    public void deleteTempUser(String email) {
        redisTemplate.delete(PREFIX + email);
    }
}

