package com.tet.tet_app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyRewardResponse {

    private Long id;
    private String name;
    private String rewardType;
    private int value;
    private String message;
    private Boolean active;
}
