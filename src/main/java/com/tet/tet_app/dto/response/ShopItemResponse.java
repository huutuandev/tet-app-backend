package com.tet.tet_app.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShopItemResponse {

    private Long id;
    private String name;
    private int price;
    private String category;
    private String imageUrl;
    private Boolean active;
}
