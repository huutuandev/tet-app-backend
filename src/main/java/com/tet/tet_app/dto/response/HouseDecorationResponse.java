package com.tet.tet_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class HouseDecorationResponse {
    private Long decorationId;
    private Long itemId;
    private String name;
    private String imageUrl;
    private int posX;
    private int posY;
    private int zIndex;
}
