package com.tet.tet_app.service;

import com.tet.tet_app.dto.response.LuckyDrawResponse;
import com.tet.tet_app.entity.*;
import com.tet.tet_app.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LuckyDrawService {
    private final LuckyDrawRepository luckyDrawRepository;
    private final LuckyRewardRepository luckyRewardRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserItemRepository userItemRepository;
    private final ShopItemRepository shopItemRepository;

    @Transactional
    public LuckyDrawResponse drawLucky(User user) {
        LocalDate today = LocalDate.now();

        // Check nếu đã bốc hôm nay
        if (luckyDrawRepository.findByUserIdAndDrawDate(user.getId(), today).isPresent()) {
            throw new RuntimeException("Bạn đã bốc lộc hôm nay rồi! Hãy quay lại mai nhé 🧧");
        }

        // Random reward (giả định có nhiều reward trong DB)
        long count = luckyRewardRepository.count();
        if (count == 0) {
            throw new RuntimeException("Chưa có lộc nào, liên hệ admin!");
        }
        Random random = new Random();
        long randomId = random.nextLong(count) + 1; // ID từ 1 đến count
        LuckyReward reward = luckyRewardRepository.findById(randomId)
                .orElseThrow(() -> new RuntimeException("Lỗi random lộc"));

        // Lưu lucky_draw
        LuckyDraw draw = LuckyDraw.builder()
                .userId(user.getId())
                .rewardId(reward.getId())
                .drawDate(today)
                .build();
        luckyDrawRepository.save(draw);

        // Xử lý reward
        if (reward.getRewardType().equals("points")) {
            Wallet wallet = walletRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ví"));
            wallet.setBalance(wallet.getBalance() + reward.getValue());
            walletRepository.save(wallet);

            // Log transaction
            WalletTransaction transaction = WalletTransaction.builder()
                    .userId(user.getId())
                    .amount(reward.getValue())
                    .type("draw")
                    .description("Bốc lộc nhận " + reward.getValue() + " điểm")
                    .build();
            walletTransactionRepository.save(transaction);
        }else if ("sticker".equals(reward.getRewardType()) || "avatar".equals(reward.getRewardType())) {
            Long itemId = (long) reward.getValue();

            // KIỂM TRA itemId có tồn tại trong shop_items không
            shopItemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Lộc này lỗi kỹ thuật (item không tồn tại). Liên hệ admin nhé!"));

            // Nếu tồn tại → mới insert user_items
            UserItem userItem = UserItem.builder()
                    .userId(user.getId())
                    .itemId(itemId)
                    .quantity(1)
                    .build();

            // Kiểm tra tránh duplicate (nếu user đã có item này)
            if (!userItemRepository.existsByUserIdAndItemId(user.getId(), itemId)) {
                userItemRepository.save(userItem);
            }
        }

        // Trả response
        return new LuckyDrawResponse(
                reward.getName(),
                reward.getRewardType(),
                reward.getValue(),
                reward.getMessage()
        );
    }
}
