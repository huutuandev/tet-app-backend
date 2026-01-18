package com.tet.tet_app.controller;

import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.dto.response.UserCheckResponse;
import com.tet.tet_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PublicUserController {

    private final UserService userService;

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<UserCheckResponse>> checkEmail(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Check email exact",
                        userService.checkEmailExact(email.trim())
                )
        );
    }

}
