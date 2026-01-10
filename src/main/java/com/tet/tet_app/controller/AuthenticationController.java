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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<Void>> register(
            @RequestBody RegisterRequest request) {

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
    public ResponseEntity<ApiResponse<AuthResponse>> verifyEmail(
            @RequestBody VerifyEmailRequest request) {

        boolean isValid = emailVerificationService.verifyCode(
                request.getEmail(),
                request.getCode()
        );

        if (!isValid) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Mã không hợp lệ", null));
        }

        TempUser tempUser = tempUserService.getTempUser(request.getEmail());

        if (tempUser == null) {
            throw new RuntimeException("User tạm đã hết hạn!");
        }

        User user = userService.createUserFromTemp(tempUser);

        tempUserService.deleteTempUser(request.getEmail());

        String jwt = authService.generateJwtForUser(user);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Xác thực thành công!",
                        AuthResponse.builder()
                                .userId(user.getId())
                                .fullName(user.getFullName())
                                .token(jwt)
                                .build()
                )
        );
    }


    @PostMapping("/resend-code")
    public ResponseEntity<ApiResponse<Void>> resendCode(
            @RequestBody ResendCodeRequest request) {

        TempUser tempUser = tempUserService.getTempUser(request.getEmail());

        if (tempUser == null) {
            throw new RuntimeException("User tạm không tồn tại hoặc đã hết hạn");
        }

        String newCode = emailVerificationService.createVerificationCode(request.getEmail());

        emailVerificationService.sendVerificationEmailAsync(
                request.getEmail()
                , newCode
                , tempUser.getFullName());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Đã gửi lại mã", null)
        );
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @RequestBody LoginRequest request) {

        try {
            AuthResponse response = authService.login(
                    request.getEmail(),
                    request.getPassword()
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Đăng nhập thành công!",
                            response
                    )
            );

        } catch (Exception e) {
            log.error("Lỗi khi đăng nhập: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    ));
        }
    }

}