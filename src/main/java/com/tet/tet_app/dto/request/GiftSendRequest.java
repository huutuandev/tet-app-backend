package com.tet.tet_app.dto.request;


import lombok.Data;

@Data
public class GiftSendRequest {
    private String email;
    private int amount;
    private String message;
}
