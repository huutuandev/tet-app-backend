package com.tet.tet_app.dto.request;

import lombok.Data;

@Data
public class WishUpdateRequest {
    private String content;
    private Boolean isPrivate;
    private Boolean enableShare;
}
