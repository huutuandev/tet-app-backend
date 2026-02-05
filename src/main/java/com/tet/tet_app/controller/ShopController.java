package com.tet.tet_app.controller;

import com.tet.tet_app.dto.request.BuyItemRequest;
import com.tet.tet_app.dto.request.ShopItemCreateRequest;
import com.tet.tet_app.dto.request.ShopItemUpdateRequest;
import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.dto.response.InventoryItemResponse;
import com.tet.tet_app.dto.response.ShopItemResponse;
import com.tet.tet_app.entity.ShopItem;
import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    //USER
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<Page<ShopItemResponse>>> getItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        var items = shopService.getShopItems(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Lấy danh sách sản phẩm thành công", items)
        );
    }
    @PostMapping("/buy")
    public ResponseEntity<?> buyItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody BuyItemRequest request) {

        shopService.buyItem(userDetails.getUser(), request.getItemId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Bạn đã mua thành công", null));
    }

    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<List<InventoryItemResponse>>> getMyInventory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        var inventory = shopService.getMyInventory(userDetails.getUser());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Lấy danh sách vật phẩm đã mua thành công",
                        inventory
                )
        );
    }

    //ADMIN
    @PostMapping("/admin/items")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShopItemResponse>> createShopItem(
            @RequestBody ShopItemCreateRequest request) {

        ShopItemResponse response = shopService.createShopItem(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Admin thêm sản phẩm vào shop thành công 🎉",
                                response
                        )
                );
    }

    @PutMapping("/admin/items/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShopItemResponse>> updateShopItem(
            @PathVariable Long id,
            @RequestBody ShopItemUpdateRequest request) {

        var response = shopService.updateShopItem(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cập nhật sản phẩm thành công", response)
        );
    }

    @DeleteMapping("/admin/items/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> disableShopItem(@PathVariable Long id) {

        shopService.disableShopItem(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Đã disable sản phẩm", null)
        );
    }

    @GetMapping("/admin/items")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ShopItemResponse>>> getAllShopItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        var items = shopService.getAllShopItems(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Admin lấy danh sách shop item", items)
        );
    }

}

