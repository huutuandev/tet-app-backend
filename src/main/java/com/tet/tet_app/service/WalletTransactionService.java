package com.tet.tet_app.service;

import com.tet.tet_app.entity.WalletTransaction;
import com.tet.tet_app.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletTransactionService {

    private final WalletTransactionRepository repository;

    // 📤 Người gửi lì xì
    public void giftSend(Long senderId, Long receiverId, String receiverName, int amount) {
        save(
                senderId,
                -amount,
                "gift_send",
                "Lì xì cho  " + receiverName
        );
    }

    // 📥 Người nhận lì xì
    public void giftReceive(Long receiverId, Long senderId,String senderName, int amount) {
        save(
                receiverId,
                amount,
                "gift_receive",
                "Nhận lì xì từ " + senderName
        );
    }

    // 🧱 Core save method
    private void save(
            Long userId,
            int amount,
            String type,
            String description
    ) {
        repository.save(
                WalletTransaction.builder()
                        .userId(userId)
                        .amount(amount)
                        .type(type)
                        .description(description)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }
}
