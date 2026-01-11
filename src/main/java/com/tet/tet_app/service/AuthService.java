package com.tet.tet_app.service;

import com.tet.tet_app.dto.response.AuthResponse;
import com.tet.tet_app.entity.User;
import com.tet.tet_app.repository.UserRepository;
import com.tet.tet_app.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    /**
     * Đăng nhập và tạo cả access token + refresh token
     * @return AuthResponse chứa access token (trong response body) và refresh token (để set cookie)
     */
    public AuthResponse login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm được tài khoản!"));

        if (!user.getIsActive()) {
            throw new RuntimeException("Tài khoản của bạn đã bị vô hiệu hóa !");
        }

        // Xác thực username/password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // Tạo access token (trả về response body)
        String accessToken = jwtService.generateAccessToken(user.getEmail());

        // Tạo refresh token (sẽ set vào cookie)
        String refreshToken = UUID.randomUUID().toString();

        // Lưu refresh token vào Redis (7 ngày)
        refreshTokenService.save(user.getId(), refreshToken, 7);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)  // ← Controller sẽ dùng để set cookie
                .userId(user.getId())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    /**
     * Tạo access token mới từ refresh token
     */
    public AuthResponse refreshAccessToken(String refreshToken) {

        Long userId = refreshTokenService.getUserId(refreshToken);

        if (userId == null) {
            throw new RuntimeException("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        if (!user.getIsActive()) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
        }

        // Tạo access token mới
        String newAccessToken = jwtService.generateAccessToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .userId(user.getId())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    /**
     * Tạo access token cho user (dùng khi verify email)
     */
    public String generateAccessToken(User user) {
        return jwtService.generateAccessToken(user.getEmail());
    }

    /**
     * Đăng xuất - xóa refresh token
     */
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenService.delete(refreshToken);
        }
    }
}