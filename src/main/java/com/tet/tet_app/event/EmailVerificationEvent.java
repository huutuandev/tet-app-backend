package com.tet.tet_app.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationEvent {
    private String email;
    private String verificationCode;
    private String fullName;
    private Long userId;
}