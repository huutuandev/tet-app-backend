package com.tet.tet_app.service;

import com.tet.tet_app.dto.response.InventoryItemResponse;
import com.tet.tet_app.entity.*;
import com.tet.tet_app.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopItemRepository shopItemRepository;
    private final UserItemRepository userItemRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    // LẤY DANH SÁCH TẤT CẢ ITEM TRONG SHOP
    public List<ShopItem> getAllItems() {
        return shopItemRepository.findAll();
    }

    // MUA ITEM
    @Transactional
    public void buyItem(User user, Long itemId) {
        // 1. Lấy item từ shop
        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vật phẩm trong cửa hàng!"));

        // 2. Lấy ví của user (wallet có user_id làm khóa chính)
        Wallet wallet = walletRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví của bạn!"));

        // 3. Kiểm tra giá (nếu price = 0 → miễn phí, cho mua thoải mái)
        if (item.getPrice() > 0 && wallet.getBalance() < item.getPrice()) {
            throw new RuntimeException("Không đủ điểm lì xì để mua vật phẩm này! Bạn cần " + item.getPrice() + " điểm.");
        }

        // 4. Trừ điểm (chỉ trừ nếu có giá > 0)
        if (item.getPrice() > 0) {
            wallet.setBalance(wallet.getBalance() - item.getPrice());
            walletRepository.save(wallet);

            // Log giao dịch trừ điểm
            WalletTransaction transaction = WalletTransaction.builder()
                    .userId(user.getId())
                    .amount(-item.getPrice())
                    .type("shop")
                    .description("Mua vật phẩm: " + item.getName())
                    .build();
            walletTransactionRepository.save(transaction);
        }

        // 5. Cộng vật phẩm vào kho người dùng
        userItemRepository.findByUserIdAndItemId(user.getId(), itemId)
                .ifPresentOrElse(
                        existingItem -> {
                            existingItem.setQuantity(existingItem.getQuantity() + 1);
                            userItemRepository.save(existingItem);
                        },
                        () -> {
                            UserItem newItem = UserItem.builder()
                                    .userId(user.getId())
                                    .itemId(itemId)
                                    .quantity(1)
                                    .build();
                            userItemRepository.save(newItem);
                        }
                );
    }
    public List<InventoryItemResponse> getMyInventory(User user) {
        return userItemRepository.findInventoryByUser(user.getId());
    }

}