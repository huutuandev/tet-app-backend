package com.tet.tet_app.security.oauth;

import com.tet.tet_app.entity.User;
import com.tet.tet_app.security.jwt.JwtService;
import com.tet.tet_app.service.RefreshTokenService;
import com.tet.tet_app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.oauth2.redirect-uri:http://localhost:5178/oauth2/callback}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String googleId = oAuth2User.getAttribute("sub");
            String avatar = oAuth2User.getAttribute("picture");

            User user = userService.registerOrGetGoogleUser(
                    googleId, email, name, avatar
            );

            String accessToken = jwtService.generateAccessToken(user.getEmail());
            String refreshToken = UUID.randomUUID().toString();
            refreshTokenService.save(user.getId(), refreshToken, 7);

            String refreshCookie = String.format(
                    "refresh_token=%s; HttpOnly; Secure; Path=/api/auth; Max-Age=%d; SameSite=Strict",
                    refreshToken,
                    7 * 24 * 60 * 60
            );
            response.addHeader("Set-Cookie", refreshCookie);

            // ✅ TẠO ONE-TIME CODE (chỉ dùng 1 lần, hết hạn sau 60s)
            String oneTimeCode = UUID.randomUUID().toString();

            // Lưu Redis: code → {accessToken, userId, name}
            String tokenData = String.format("%s|%s|%s",
                    accessToken, user.getId(), user.getFullName());

            redisTemplate.opsForValue().set(
                    "oauth2:code:" + oneTimeCode,
                    tokenData,
                    60,  // 60 giây
                    TimeUnit.SECONDS
            );

            // Redirect với ONE-TIME CODE thay vì token
            String targetUrl = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                    .queryParam("code", oneTimeCode)  // ← Chỉ gửi code
                    .build()
                    .toUriString();

            log.info("OAuth2 login thành công cho user: {}", email);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            log.error("Lỗi OAuth2 authentication: {}", e.getMessage());
            response.sendRedirect(frontendRedirectUri + "?error=oauth2_failed");
        }
    }
}