package com.tet.tet_app.dto.request;
import lombok.Data;

@Data
public class WishCreateRequest {
    private Long receiverId;     // null = public
    private String content;
    private boolean isPrivate;
    private boolean enableShare;
}