package com.tet.tet_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GiftResponse {
    private boolean success;
    private String message;
    private int senderBalance;
}
