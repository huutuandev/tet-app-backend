package com.tet.tet_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InventoryItemResponse {
    private Long itemId;
    private String name;
    private String imageUrl;
    private int quantity;
}

