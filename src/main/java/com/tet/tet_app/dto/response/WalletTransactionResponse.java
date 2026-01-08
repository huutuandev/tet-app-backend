package com.tet.tet_app.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransactionResponse {

    private Long id;

    private Long userId;

    private int amount;

    private String type;

    private String description;

    private LocalDateTime createdAt;
}
