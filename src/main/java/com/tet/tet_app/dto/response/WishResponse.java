package com.tet.tet_app.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder

public class WishResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private boolean isPrivate;
    private String shareToken;
    private LocalDateTime createdAt;
}
