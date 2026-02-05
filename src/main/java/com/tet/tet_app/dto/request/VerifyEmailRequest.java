package com.tet.tet_app.dto.request;

import lombok.Data;

@Data
public class VerifyEmailRequest {
    private String email;
    private String code;
}
