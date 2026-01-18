package com.tet.tet_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCheckResponse {
    private boolean exists;
    private boolean active;
    private String fullName;
    private String avatarUrl;
}
