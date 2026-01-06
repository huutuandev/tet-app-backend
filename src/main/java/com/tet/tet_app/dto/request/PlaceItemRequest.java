package com.tet.tet_app.dto.request;

import lombok.Getter;

@Getter
public class PlaceItemRequest {
    private Long itemId;
    private int posX;
    private int posY;
    private int zIndex;
}
