package com.tet.tet_app.dto.request;

import lombok.Data;

@Data
public class LuckyRewardUpdateRequest {

    private String name;
    private String rewardType; // message, points, sticker, avatar
    private Integer value;
    private String message;
    private Boolean active; // nếu bạn muốn disable thay vì xóa cứng
}
