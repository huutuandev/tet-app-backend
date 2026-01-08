package com.tet.tet_app.controller;

import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.dto.response.LuckyDrawResponse;
import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.LuckyDrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lucky-draw")
@RequiredArgsConstructor
public class LuckyDrawController {
    private final LuckyDrawService luckyDrawService;

    @PostMapping("/bock")
    public ResponseEntity<ApiResponse<LuckyDrawResponse>> bockLucky(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        LuckyDrawResponse response =
                luckyDrawService.drawLucky(currentUser.getUser());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Bốc lộc thành công",
                        response
                )
        );
    }

}
