package com.tet.tet_app.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String fullName;
    private String username;
    private boolean enabled;
    private List<String> roles;
}
