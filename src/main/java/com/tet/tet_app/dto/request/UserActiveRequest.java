package com.tet.tet_app.dto.request;

import lombok.Data;

@Data
public class UserActiveRequest {
    private Boolean active; // true = bật, false = tắt
}
