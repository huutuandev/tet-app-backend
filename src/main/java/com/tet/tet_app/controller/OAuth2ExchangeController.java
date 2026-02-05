package com.tet.tet_app.controller;

import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/oauth2")
@RequiredArgsConstructor
@Slf4j
public class OAuth2ExchangeController {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String CODE_PREFIX = "oauth2:code:";

    /**
     * Exchange one-time code → access token
     * Code chỉ dùng được 1 lần và hết hạn sau 60s
     */
    @PostMapping("/exchange")
    public ResponseEntity<ApiResponse<AuthResponse>> exchangeCode(
            @RequestBody Map<String, String> request) {

        String code = request.get("code");

        if (code == null || code.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse<>(false, "Code không được để trống", null));
        }

        try {
            // Lấy token data từ Redis
            String tokenData = redisTemplate.opsForValue().get(CODE_PREFIX + code);

            if (tokenData == null) {
                log.warn("Code không hợp lệ hoặc đã hết hạn: {}", code);
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Code không hợp lệ hoặc đã hết hạn", null));
            }

            // ✅ XÓA CODE NGAY (one-time use)
            redisTemplate.delete(CODE_PREFIX + code);

            // Parse: "accessToken|userId|fullName"
            String[] parts = tokenData.split("\\|", 3);

            if (parts.length < 3) {
                log.error("Token data format không hợp lệ: {}", tokenData);
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(false, "Lỗi hệ thống", null));
            }

            String accessToken = parts[0];
            Long userId = Long.valueOf(parts[1]);
            String fullName = parts[2];

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(accessToken)
                    .userId(userId)
                    .fullName(fullName)
                    .build();

            log.info("Exchange code thành công cho userId: {}", userId);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Đăng nhập thành công", authResponse)
            );

        } catch (NumberFormatException e) {
            log.error("Lỗi parse userId: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Dữ liệu không hợp lệ", null));

        } catch (Exception e) {
            log.error("Lỗi exchange code: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi", null));
        }
    }
}