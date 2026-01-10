package com.tet.tet_app.dto.request;

import com.tet.tet_app.entity.enums.ShopItemCategory;
import lombok.Data;

@Data
public class ShopItemCreateRequest {

    private String name;
    private int price;
    private ShopItemCategory category;
    private String imageUrl;
}
