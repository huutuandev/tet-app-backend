package com.tet.tet_app.redis.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempUser {
    private String email;
    private String passwordHash;
    private String fullName;
}
