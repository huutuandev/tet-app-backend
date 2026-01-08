package com.tet.tet_app.controller;

import com.tet.tet_app.dto.request.BuyItemRequest;
import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.dto.response.InventoryItemResponse;
import com.tet.tet_app.entity.ShopItem;
import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<ShopItem>>> getItems() {

        var items = shopService.getAllItems();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Lấy danh sách vật phẩm shop thành công",
                        items
                )
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

}

