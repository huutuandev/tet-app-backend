package com.tet.tet_app.controller;

import com.tet.tet_app.dto.request.WishCreateRequest;
import com.tet.tet_app.dto.request.WishUpdateRequest;
import com.tet.tet_app.dto.response.WishResponse;
import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishes")
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    // ✅ Tạo lời chúc (JWT)
    @PostMapping
    public WishResponse createWish(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody WishCreateRequest request
    ) {
        return wishService.createWish(currentUser, request);
    }

    // 📤 Đã gửi
    @GetMapping("/sent")
    public Page<WishResponse> sent(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Pageable pageable
    ) {
        return wishService.getSent(currentUser.getUser().getId(), pageable);
    }

    // 🔍 Chi tiết
    @GetMapping("/{id}")
    public WishResponse detail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return wishService.getWishById(id, currentUser.getUser().getId());
    }
    @PutMapping("/{id}")
    public WishResponse updateWish(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody WishUpdateRequest request
    ) {
        return wishService.updateWish(id, currentUser.getUser().getId(), request);
    }


    // 🔗 Share
    @GetMapping("/share/{token}")
    public WishResponse share(@PathVariable String token) {
        return wishService.getWishByShareToken(token);
    }

    // 🗑 Xóa
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        wishService.deleteWish(id, currentUser.getUser().getId());
    }
}
