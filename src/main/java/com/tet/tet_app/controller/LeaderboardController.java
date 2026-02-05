package com.tet.tet_app.controller;

import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.dto.response.LeaderboardResponse;
import com.tet.tet_app.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final WalletService leaderboardService;

    @GetMapping("/top10")
    public ResponseEntity<ApiResponse<List<LeaderboardResponse>>> getTop10() {

        var top10 = leaderboardService.getTop10();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Lấy bảng xếp hạng top 10 thành công",
                        top10
                )
        );
    }

}

