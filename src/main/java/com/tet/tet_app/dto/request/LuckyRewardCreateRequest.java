package com.tet.tet_app.dto.request;

import lombok.Data;

@Data
public class LuckyRewardCreateRequest {
    private String name;
    private String rewardType; // message, points, sticker, avatar
    private int value;         // points = số điểm, sticker/avatar = itemId
    private String message;
}
