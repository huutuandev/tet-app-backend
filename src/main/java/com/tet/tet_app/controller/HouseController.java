package com.tet.tet_app.controller;

import com.tet.tet_app.dto.request.PlaceItemRequest;
import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.dto.response.InventoryItemResponse;
import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/house")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;

    // 🏠 Nhà của mình
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> myHouse(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        var house = houseService.getMyHouse(userDetails.getUser());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Lấy thông tin nhà thành công",
                        house
                )
        );
    }

    // 👀 Xem nhà người khác (share)
    @GetMapping("/by-id/{userId}")
    public ResponseEntity<ApiResponse<?>> userHouseById(@PathVariable Long userId) {

        var house = houseService.getUserHouseById(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Lấy thông tin nhà theo ID thành công",
                        house
                )
        );
    }

    // ➕ Đặt item vào nhà
    @PostMapping("/place")
    public ResponseEntity<?> placeItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PlaceItemRequest request) {

        houseService.placeItem(userDetails.getUser(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Đã đặt vật phẩm vào nhà", null));
    }

    // ✏️ Sửa vị trí
    @PutMapping("/{decorationId}")
    public ResponseEntity<?> updateDecoration(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long decorationId,
            @RequestBody PlaceItemRequest request) {

        houseService.updateDecoration(userDetails.getUser(), decorationId, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Đã cập nhật vị trí", null)
        );
    }

    // ❌ Xóa khỏi nhà
    @DeleteMapping("/{decorationId}")
    public ResponseEntity<?> removeDecoration(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long decorationId) {

        houseService.removeDecoration(userDetails.getUser(), decorationId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<?>> publicHouse(@PathVariable String slug) {

        var house = houseService.getUserHouseBySlug(slug);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Lấy thông tin nhà theo slug thành công",
                        house
                )
        );
    }

}
