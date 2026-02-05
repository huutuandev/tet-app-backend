package com.tet.tet_app.controller;

import com.tet.tet_app.dto.request.LuckyRewardCreateRequest;
import com.tet.tet_app.dto.request.LuckyRewardUpdateRequest;
import com.tet.tet_app.dto.response.AdminLuckyRewardResponse;
import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.dto.response.LuckyDrawResponse;
import com.tet.tet_app.dto.response.LuckyRewardResponse;
import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.LuckyDrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    // ================= ADMIN =================
    @GetMapping("/admin/reward")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<LuckyRewardResponse>>> getAllLuckyRewards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        Page<LuckyRewardResponse> rewards =
                luckyDrawService.getAllLuckyRewardsForAdmin(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Admin lấy danh sách tất cả lộc",
                        rewards
                )
        );
    }

    @PostMapping("/admin/reward")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminLuckyRewardResponse>> createLuckyReward(
            @RequestBody LuckyRewardCreateRequest req) {

        AdminLuckyRewardResponse response =
                luckyDrawService.createLuckyReward(req);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Admin tạo lộc thành công 🎉",
                        response
                )
        );
    }


    @PutMapping("/admin/reward/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminLuckyRewardResponse>> updateLuckyReward(
            @PathVariable Long id,
            @RequestBody LuckyRewardUpdateRequest req) {

        var response = luckyDrawService.updateLuckyReward(id, req);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cập nhật lộc thành công", response)
        );
    }

    @DeleteMapping("/admin/reward/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLuckyReward(
            @PathVariable Long id) {

        luckyDrawService.deleteLuckyReward(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Đã xóa (disable) lộc", null)
        );
    }

}
