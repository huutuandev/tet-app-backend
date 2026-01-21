package com.tet.tet_app.security.jwt;

import com.tet.tet_app.entity.Role;
import com.tet.tet_app.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-expiration-ms}")
    private long accessExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    /* ================= CREATE TOKEN ================= */

    /**
     * Tạo access token (15 phút)
     */
    public String generateAccessToken(User user) {
        return buildToken(user, accessExpirationMs);
    }

    /**
     * Tạo refresh token (7 ngày) - Không dùng nữa, dùng UUID thay thế
     * @deprecated Sử dụng UUID.randomUUID() cho refresh token
     */
    @Deprecated
    public String generateRefreshToken(User user) {
        return buildToken(user, refreshExpirationMs);
    }

    /**
     * Build JWT token
     */
    private String buildToken(User user, long expirationMs) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("roles",
                        user.getRoles()
                                .stream()
                                .map(Role::getName)
                                .toList()
                )
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(getSignKey())
                .compact();
    }


    /* ================= PARSE & EXTRACT ================= */

    /**
     * Lấy email từ token
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Lấy expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract một claim cụ thể từ token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);  // ✅ ÁP DỤNG resolver LÊN claims
    }

    /**
     * Parse token và lấy tất cả claims
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            log.warn("JWT token đã hết hạn: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.error("JWT token không hợp lệ: {}", e.getMessage());
            throw e;
        }
    }

    /* ================= VALIDATION ================= */

    /**
     * Kiểm tra token đã hết hạn chưa
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Validate token với email
     */
    public boolean validateToken(String token, String email) {
        try {
            final String tokenEmail = extractEmail(token);
            return (tokenEmail.equals(email) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /* ================= SECRET KEY ================= */

    /**
     * Tạo signing key từ secret
     */
    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}