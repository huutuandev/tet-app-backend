package com.tet.tet_app.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LuckyRewardResponse {

    private Long id;
    private String name;
    private String rewardType;
    private int value;
    private String message;
    private Boolean active;
}
