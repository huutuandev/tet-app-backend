package com.tet.tet_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LuckyDrawResponse {
    private String rewardName;
    private String rewardType;
    private int value;
    private String message;
}
