package com.tet.tet_app.controller;

import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.HoroscopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/horoscope")
@RequiredArgsConstructor
public class HoroscopeController {

    private final HoroscopeService horoscopeService;

    @GetMapping("/{category}")
    public ResponseEntity<ApiResponse<Object>> view(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String category
    ) {

        String message = horoscopeService.viewTodayHoroscope(
                user.getUser().getId(),
                category
        );

        var data = new java.util.HashMap<String, Object>();
        data.put("category", category);
        data.put("message", message);
        data.put("date", LocalDate.now());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Xem tử vi hôm nay thành công",
                        data
                )
        );
    }
}
