package com.tet.tet_app.controller;

import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.TetAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/tet-ai")
@RequiredArgsConstructor
public class TetAIController {

    private final TetAIService tetAIService;

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<String>> ask(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody Map<String, String> req) {

        try {
            String answer = tetAIService.ask(
                    user.getUser().getId(),
                    req.get("question")
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Câu trả lời của AI",
                            answer
                    )
            );

        } catch (RuntimeException e) {
            if ("LIMIT_EXCEEDED".equals(e.getMessage())) {
                return ResponseEntity
                        .badRequest()
                        .body(
                                new ApiResponse<>(
                                        false,
                                        "Bạn đã hỏi đủ 5 câu hôm nay 🌸. Hãy quay lại vào ngày mai nhé!",
                                        null
                                )
                        );
            }
            throw e;
        }
    }
}


