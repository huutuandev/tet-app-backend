package com.tet.tet_app.controller;

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

    // LẤY PROFILE
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        return ResponseEntity.ok(profileService.getProfile(currentUser.getUser()));
    }

    // CẬP NHẬT PROFILE
    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody ProfileUpdateRequest request) {

        return ResponseEntity.ok(profileService.updateProfile(currentUser.getUser(), request));
    }
}