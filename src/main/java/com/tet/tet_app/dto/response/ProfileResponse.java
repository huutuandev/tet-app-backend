package com.tet.tet_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String favoriteQuote;
    private int points;
}
