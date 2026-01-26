package com.tet.tet_app.controller;

import com.tet.tet_app.dto.request.LoginRequest;
import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.dto.request.RegisterRequest;
import com.tet.tet_app.dto.request.ResendCodeRequest;
import com.tet.tet_app.dto.request.VerifyEmailRequest;
import com.tet.tet_app.dto.response.AuthResponse;
import com.tet.tet_app.entity.User;
import com.tet.tet_app.redis.model.TempUser;
import com.tet.tet_app.redis.service.TempUserService;
import com.tet.tet_app.service.AuthService;
import com.tet.tet_app.service.EmailVerificationService;
import com.tet.tet_app.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    private final AuthService authService;
    private final TempUserService tempUserService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequest request) {

        try {
            userService.registerUser(
                    request.getEmail(),
                    request.getPassword(),
                    request.getFullName()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.",
                            null
                    ));

        } catch (Exception e) {
            log.error("Lỗi khi đăng ký: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyEmail(@RequestBody VerifyEmailRequest request) {

        try {
            boolean isValid = emailVerificationService.verifyCode(
                    request.getEmail(),
                    request.getCode()
            );

            if (!isValid) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Mã xác thực không hợp lệ hoặc đã hết hạn", null));
            }

            TempUser tempUser = tempUserService.getTempUser(request.getEmail());

            if (tempUser == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Phiên đăng ký đã hết hạn. Vui lòng đăng ký lại.", null));
            }

            // Tạo user từ temp user
            User user = userService.createUserFromTemp(tempUser);

            // Xóa temp user
            tempUserService.deleteTempUser(request.getEmail());

            // Tạo access token
            String accessToken = authService.generateAccessToken(user);

            // Trả về access token trong response body (không set cookie cho verify)
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Xác thực email thành công!",
                            AuthResponse.builder()
                                    .userId(user.getId())
                                    .fullName(user.getFullName())
                                    .accessToken(accessToken)
                                    .build()
                    )
            );

        } catch (Exception e) {
            log.error("Lỗi khi xác thực email: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi. Vui lòng thử lại.", null));
        }
    }

    @PostMapping("/resend-code")
    public ResponseEntity<ApiResponse<Void>> resendCode(@RequestBody ResendCodeRequest request) {

        try {
            TempUser tempUser = tempUserService.getTempUser(request.getEmail());

            if (tempUser == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Phiên đăng ký không tồn tại hoặc đã hết hạn", null));
            }

            String newCode = emailVerificationService.createVerificationCode(request.getEmail());

            emailVerificationService.sendVerificationEmailAsync(
                    request.getEmail(),
                    newCode,
                    tempUser.getFullName()
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Đã gửi lại mã xác thực", null)
            );

        } catch (Exception e) {
            log.error("Lỗi khi gửi lại mã: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Không thể gửi lại mã. Vui lòng thử lại.", null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {

        try {
            // AuthService xử lý login và tạo cả access + refresh token
            AuthResponse authResponse = authService.login(
                    request.getEmail(),
                    request.getPassword()
            );

            // Set refresh token vào HTTP-only cookie
            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", authResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)        // ✅ Đổi từ true → false (vì local không dùng HTTPS)
                    .path("/api/auth")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Lax")     // ✅ Đổi từ "Strict" → "Lax" hoặc bỏ dòng này
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            // Không trả refresh token trong response body (chỉ trong cookie)
            AuthResponse responseData = AuthResponse.builder()
                    .accessToken(authResponse.getAccessToken())
                    .userId(authResponse.getUserId())
                    .fullName(authResponse.getFullName())
                    .avatarUrl(authResponse.getAvatarUrl())
                    .build();

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Đăng nhập thành công!",
                            responseData
                    )
            );

        } catch (Exception e) {
            log.error("Lỗi khi đăng nhập: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {

        try {
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Không tìm thấy refresh token", null));
            }

            // AuthService xử lý refresh
            AuthResponse authResponse = authService.refreshAccessToken(refreshToken);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Làm mới token thành công",
                            authResponse
                    )
            );

        } catch (Exception e) {
            log.error("Lỗi khi refresh token: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {

        try {
            // Xóa refresh token khỏi Redis
            authService.logout(refreshToken);

            // Xóa cookie
            ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                    .httpOnly(true)
                    .secure(false)        // ✅ Đổi từ true → false
                    .path("/api/auth")
                    .maxAge(0)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Đăng xuất thành công", null)
            );

        } catch (Exception e) {
            log.error("Lỗi khi đăng xuất: {}", e.getMessage());
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Đăng xuất thành công", null)
            );
        }
    }
}