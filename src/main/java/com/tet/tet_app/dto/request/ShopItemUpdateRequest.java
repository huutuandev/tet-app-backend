package com.tet.tet_app.dto.request;

import com.tet.tet_app.entity.enums.ShopItemCategory;
import lombok.Data;

@Data
public class ShopItemUpdateRequest {

    private String name;
    private Integer price;
    private ShopItemCategory category;
    private String imageUrl;
    private Boolean active;
}
