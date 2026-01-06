package com.tet.tet_app.controller;

import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.dto.response.ProfileResponse;
import com.tet.tet_app.dto.request.ProfileUpdateRequest;
import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // ⭐ LẤY PROFILE
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        var profile = profileService.getProfile(currentUser.getUser());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Lấy thông tin hồ sơ thành công",
                        profile
                )
        );
    }

    // ⭐ CẬP NHẬT PROFILE
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody ProfileUpdateRequest request) {

        var updated = profileService.updateProfile(currentUser.getUser(), request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Cập nhật hồ sơ thành công",
                        updated
                )
        );
    }
}
