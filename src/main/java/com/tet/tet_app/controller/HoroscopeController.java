package com.tet.tet_app.controller;

import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.HoroscopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/horoscope")
@RequiredArgsConstructor
public class HoroscopeController {

    private final HoroscopeService horoscopeService;

    @GetMapping("/{category}")
    public ResponseEntity<?> view(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String category
    ) {
        String message =
                horoscopeService.viewTodayHoroscope(
                        user.getUser().getId(), category
                );

        return ResponseEntity.ok(Map.of(
                "category", category,
                "message", message,
                "date", LocalDate.now()
        ));
    }
}

