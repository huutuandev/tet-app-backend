package com.tet.tet_app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopItemResponse {

    private Long id;
    private String name;
    private int price;
    private String category;
    private String imageUrl;
    private Boolean active;
}
