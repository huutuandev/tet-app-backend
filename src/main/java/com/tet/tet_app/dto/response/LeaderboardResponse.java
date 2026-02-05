package com.tet.tet_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LeaderboardResponse {

    private Long userId;
    private String fullName;
    private String avatarUrl;
    private int points;
}

