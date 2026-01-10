package com.tet.tet_app.service;

import com.tet.tet_app.dto.request.LuckyRewardCreateRequest;
import com.tet.tet_app.dto.request.LuckyRewardUpdateRequest;
import com.tet.tet_app.dto.response.AdminLuckyRewardResponse;
import com.tet.tet_app.dto.response.LuckyDrawResponse;
import com.tet.tet_app.dto.response.LuckyRewardResponse;
import com.tet.tet_app.entity.*;
import com.tet.tet_app.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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
        long count = luckyRewardRepository.countByActiveTrue();
        if (count == 0) {
            throw new RuntimeException("Chưa có lộc nào đang hoạt động, liên hệ admin!");
        }

        List<LuckyReward> activeRewards = luckyRewardRepository.findByActiveTrue();

        Random random = new Random();
        LuckyReward reward = activeRewards.get(random.nextInt(activeRewards.size()));

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

    //ADMIN
    @Transactional
    public Page<LuckyRewardResponse> getAllLuckyRewardsForAdmin(Pageable pageable) {

        return luckyRewardRepository.findAll(pageable)
                .map(reward -> LuckyRewardResponse.builder()
                        .id(reward.getId())
                        .name(reward.getName())
                        .rewardType(reward.getRewardType())
                        .value(reward.getValue())
                        .message(reward.getMessage())
                        .active(reward.isActive())
                        .build()
                );
    }

    @Transactional
    public AdminLuckyRewardResponse createLuckyReward(LuckyRewardCreateRequest req) {

        if (req.getName() == null || req.getRewardType() == null) {
            throw new RuntimeException("Thiếu thông tin lộc");
        }

        LuckyReward reward = LuckyReward.builder()
                .name(req.getName())
                .rewardType(req.getRewardType())
                .value(req.getValue())
                .message(req.getMessage())
                .build();

        LuckyReward saved = luckyRewardRepository.save(reward);

        return AdminLuckyRewardResponse.builder()
                .status("CREATED")
                .adminAction("CREATE_LUCKY_REWARD")
                .reward(
                        LuckyRewardResponse.builder()
                                .id(saved.getId())
                                .name(saved.getName())
                                .rewardType(saved.getRewardType())
                                .value(saved.getValue())
                                .message(saved.getMessage())
                                .build()
                )
                .build();
    }

    @Transactional
    public AdminLuckyRewardResponse updateLuckyReward(Long id, LuckyRewardUpdateRequest req) {

        LuckyReward reward = luckyRewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lộc"));

        if (req.getName() != null) reward.setName(req.getName());
        if (req.getRewardType() != null) reward.setRewardType(req.getRewardType());
        if (req.getValue() != null) reward.setValue(req.getValue());
        if (req.getMessage() != null) reward.setMessage(req.getMessage());
        if (req.getActive() != null) reward.setActive(req.getActive());

        LuckyReward saved = luckyRewardRepository.save(reward);

        return AdminLuckyRewardResponse.builder()
                .status("UPDATED")
                .adminAction("UPDATE_LUCKY_REWARD")
                .reward(
                        LuckyRewardResponse.builder()
                                .id(saved.getId())
                                .name(saved.getName())
                                .rewardType(saved.getRewardType())
                                .value(saved.getValue())
                                .message(saved.getMessage())
                                .build()
                )
                .build();
    }

    @Transactional
    public void deleteLuckyReward(Long id) {

        LuckyReward reward = luckyRewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lộc"));

        // ❌ XÓA CỨNG (không khuyên)
        // luckyRewardRepository.delete(reward);

        // ✅ DISABLE (KHUYÊN)
        reward.setActive(false);
    }

}
