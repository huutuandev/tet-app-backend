package com.tet.tet_app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // Không serialize null fields
public class AuthResponse {

    private String accessToken;      // Trả về trong response body

    private String refreshToken;     // Chỉ dùng nội bộ, không serialize ra JSON

    private Long userId;

    private String fullName;

    private String avatarUrl;
}