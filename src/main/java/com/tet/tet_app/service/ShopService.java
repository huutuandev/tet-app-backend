package com.tet.tet_app.service;

import com.tet.tet_app.dto.request.ShopItemCreateRequest;
import com.tet.tet_app.dto.request.ShopItemUpdateRequest;
import com.tet.tet_app.dto.response.InventoryItemResponse;
import com.tet.tet_app.dto.response.ShopItemResponse;
import com.tet.tet_app.entity.*;
import com.tet.tet_app.entity.enums.ShopItemCategory;
import com.tet.tet_app.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopItemRepository shopItemRepository;
    private final UserItemRepository userItemRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    // MUA ITEM
    @Transactional
    public void buyItem(User user, Long itemId) {
        // 1. Lấy item từ shop
        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vật phẩm"));

        if (!item.isActive()) {
            throw new RuntimeException("Vật phẩm này hiện không còn bán");
        }

        if (item.getCategory() == ShopItemCategory.AVATAR &&
                userItemRepository.existsByUserIdAndItemId(user.getId(), itemId)) {
            throw new RuntimeException("Bạn đã sở hữu avatar này rồi");
        }

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

    @Transactional
    public ShopItemResponse createShopItem(ShopItemCreateRequest req) {

        if (req.getName() == null || req.getName().isBlank()) {
            throw new RuntimeException("Tên sản phẩm không được để trống");
        }

        if (req.getPrice() < 0) {
            throw new RuntimeException("Giá sản phẩm không hợp lệ");
        }

        ShopItem item = ShopItem.builder()
                .name(req.getName())
                .price(req.getPrice())
                .category(req.getCategory())
                .imageUrl(req.getImageUrl())
                .active(Boolean.TRUE)
                .build();

        ShopItem saved = shopItemRepository.save(item);

        return mapToAdminResponse(saved);
    }

    @Transactional
    public ShopItemResponse updateShopItem(Long id, ShopItemUpdateRequest req) {

        ShopItem item = shopItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (req.getName() != null) item.setName(req.getName());
        if (req.getPrice() != null) item.setPrice(req.getPrice());
        if (req.getCategory() != null) item.setCategory(req.getCategory());
        if (req.getImageUrl() != null) item.setImageUrl(req.getImageUrl());
        if (req.getActive() != null) item.setActive(req.getActive());

        ShopItem saved = shopItemRepository.save(item);

        return mapToAdminResponse(saved);
    }

    @Transactional
    public void disableShopItem(Long id) {
        ShopItem item = shopItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        item.setActive(false);
        shopItemRepository.save(item);
    }

    // LẤY DANH SÁCH TẤT CẢ ITEM TRONG SHOP
    public Page<ShopItemResponse> getShopItems(Pageable pageable) {
        return shopItemRepository.findByActiveTrue(pageable)
                .map(this::mapToResponse);
    }

    // ADMIN - lấy tất cả item (kể cả inactive)
    public Page<ShopItemResponse> getAllShopItems(Pageable pageable) {
        return shopItemRepository.findAll(pageable)
                .map(this::mapToAdminResponse);
    }


    /* ===== Mapper ===== */
    private ShopItemResponse mapToResponse(ShopItem item) {
        return ShopItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .category(item.getCategory().name())
                .imageUrl(item.getImageUrl())
                .build();
    }

    private ShopItemResponse mapToAdminResponse(ShopItem item) {
        return ShopItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .category(item.getCategory().name())
                .imageUrl(item.getImageUrl())
                .active(item.isActive())
                .build();
    }

}