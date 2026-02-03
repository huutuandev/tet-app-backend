package com.tet.tet_app.controller;

import com.tet.tet_app.dto.request.WishCreateRequest;
import com.tet.tet_app.dto.request.WishUpdateRequest;
import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.dto.response.WishResponse;
import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wishes")
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    // ✅ Tạo lời chúc
    @PostMapping
    public ResponseEntity<?> createWish(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody WishCreateRequest request
    ) {
        WishResponse result = wishService.createWish(currentUser, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Tạo lời chúc thành công",
                        result
                )
        );
    }

    // 📤 Danh sách đã gửi
    @GetMapping("/sent")
    public ResponseEntity<?> sent(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Pageable pageable
    ) {
        Page<WishResponse> data =
                wishService.getSent(currentUser.getUser().getId(), pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Danh sách lời chúc đã gửi",
                        data
                )
        );
    }

    // 🔍 Chi tiết lời chúc
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Chi tiết lời chúc",
                        wishService.getWishById(id, currentUser.getUser().getId())
                )
        );
    }

    // ✏️ Cập nhật lời chúc
    @PutMapping("/{id}")
    public ResponseEntity<?> updateWish(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody WishUpdateRequest request
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Cập nhật thành công",
                        wishService.updateWish(id, currentUser.getUser().getId(), request)
                )
        );
    }

    // 🔗 Share public
    @GetMapping("/share/{token}")
    public ResponseEntity<?> share(@PathVariable String token) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Xem lời chúc",
                        wishService.getWishByShareToken(token)
                )
        );
    }

    // 🗑 Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        wishService.deleteWish(id, currentUser.getUser().getId());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Xóa lời chúc thành công",
                        null
                )
        );
    }

    @GetMapping("/share/sender/{sender_id}")
    public  ResponseEntity<?> getNameSender(@PathVariable Long sender_id){

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Lấy tên người gửi thành công",
                        Map.of("fullName", wishService.getSenderName(sender_id))
                )
        );
    }
}
