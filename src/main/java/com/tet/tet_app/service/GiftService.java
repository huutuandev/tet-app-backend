package com.tet.tet_app.service;

import com.tet.tet_app.dto.request.GiftSendRequest;
import com.tet.tet_app.dto.response.GiftResponse;
import com.tet.tet_app.entity.Gift;
import com.tet.tet_app.entity.User;
import com.tet.tet_app.entity.Wallet;
import com.tet.tet_app.entity.WalletTransaction;
import com.tet.tet_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GiftService {

    private final GiftRepository giftRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletTransactionService walletTransactionService;

    @Transactional
    public GiftResponse sendGift(User sender, GiftSendRequest request) {

        Long senderId = sender.getId();
        Long receiverId = request.getReceiverId();
        int amount = request.getAmount();

        if (senderId.equals(receiverId)) {
            throw new RuntimeException("Không thể tự lì xì cho chính mình");
        }

        if (amount <= 0) {
            throw new RuntimeException("Số điểm không hợp lệ");
        }
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Người nhận không tồn tại"));

        Wallet senderWallet = walletRepository.findByUserId(senderId)
                .orElseThrow(() -> new RuntimeException("Ví người gửi không tồn tại"));

        Wallet receiverWallet = walletRepository.findByUserId(receiverId)
                .orElseThrow(() -> new RuntimeException("Ví người nhận không tồn tại"));

        if (senderWallet.getBalance() < amount) {
            throw new RuntimeException("Không đủ điểm để lì xì");
        }

        // 1️⃣ Update wallet
        senderWallet.setBalance(senderWallet.getBalance() - amount);
        receiverWallet.setBalance(receiverWallet.getBalance() + amount);

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // 2️⃣ Lưu gift (PHỤC VỤ ADMIN / REPORT)
        giftRepository.save(
                Gift.builder()
                        .senderId(senderId)
                        .receiverId(receiverId)
                        .amount(amount)
                        .message(request.getMessage())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        walletTransactionService.giftSend(
                senderId,
                receiverId,
                receiver.getFullName(),
                amount
        );

        walletTransactionService.giftReceive(
                receiverId,
                senderId,
                sender.getFullName(),
                amount
        );


        return new GiftResponse(
                true,
                "Lì xì thành công 🎉",
                senderWallet.getBalance()
        );
    }
}
