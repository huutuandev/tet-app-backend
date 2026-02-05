package com.tet.tet_app.service;

import com.tet.tet_app.dto.request.PlaceItemRequest;
import com.tet.tet_app.dto.response.HouseDecorationResponse;
import com.tet.tet_app.entity.*;
import com.tet.tet_app.repository.*;
import com.tet.tet_app.until.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseService {

    private final HouseRepository houseRepository;
    private final HouseDecorationRepository houseDecorationRepository;
    private final UserItemRepository userItemRepository;
    private final ShopItemRepository shopItemRepository;

    /* =========================
       🏠 NHÀ CỦA MÌNH
       ========================= */
    @Transactional(readOnly = true)
    public List<HouseDecorationResponse> getMyHouse(User user) {

        House house = houseRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Nhà không tồn tại"));

        return mapDecorations(house.getDecorations());
    }

    /* =========================
       👀 XEM NHÀ NGƯỜI KHÁC (SHARE TOKEN)
       ========================= */
    @Transactional(readOnly = true)
    public List<HouseDecorationResponse> getHouseByShareToken(String token) {

        House house = houseRepository.findByShareToken(token)
                .orElseThrow(() -> new RuntimeException("Nhà không tồn tại"));

        return mapDecorations(house.getDecorations());
    }

    /* =========================
       ➕ ĐẶT ITEM VÀO NHÀ
       ========================= */
    @Transactional
    public void placeItem(User user, PlaceItemRequest request) {

        House house = houseRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Nhà không tồn tại"));

        Long itemId = request.getItemId();

        // 1️⃣ Kiểm tra inventory
        UserItem userItem = userItemRepository
                .findByUserIdAndItemId(user.getId(), itemId)
                .orElseThrow(() -> new RuntimeException("Bạn chưa sở hữu vật phẩm này"));

        if (userItem.getQuantity() <= 0) {
            throw new RuntimeException("Không đủ số lượng vật phẩm");
        }

        // 2️⃣ Trừ inventory
        userItem.setQuantity(userItem.getQuantity() - 1);
        if (userItem.getQuantity() == 0) {
            userItemRepository.delete(userItem);
        }

        // 3️⃣ Đặt decoration
        HouseDecoration decoration = HouseDecoration.builder()
                .house(house)
                .itemId(itemId)
                .posX(request.getPosX())
                .posY(request.getPosY())
                .zIndex(request.getZIndex())
                .build();

        house.getDecorations().add(decoration);
        // ❗ KHÔNG cần save decoration (cascade)
    }

    /* =========================
       ✏️ CẬP NHẬT VỊ TRÍ
       ========================= */
    @Transactional
    public void updateDecoration(User user, Long decorationId, PlaceItemRequest request) {

        HouseDecoration decoration = houseDecorationRepository.findById(decorationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy decoration"));

        // check ownership
        if (!decoration.getHouse().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Không có quyền sửa nhà người khác");
        }

        decoration.setPosX(request.getPosX());
        decoration.setPosY(request.getPosY());
        decoration.setZIndex(request.getZIndex());
    }

    /* =========================
       ❌ GỠ ITEM KHỎI NHÀ
       ========================= */
    @Transactional
    public void removeDecoration(User user, Long decorationId) {

        HouseDecoration decoration = houseDecorationRepository.findById(decorationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy decoration"));

        House house = decoration.getHouse();

        if (!house.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Không có quyền sửa nhà người khác");
        }

        Long itemId = decoration.getItemId();

        // 1️⃣ Xóa khỏi nhà
        house.getDecorations().remove(decoration);

        // 2️⃣ Trả lại inventory
        userItemRepository.findByUserIdAndItemId(user.getId(), itemId)
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + 1),
                        () -> userItemRepository.save(
                                UserItem.builder()
                                        .userId(user.getId())
                                        .itemId(itemId)
                                        .quantity(1)
                                        .build()
                        )
                );
    }

    /* =========================
       🔁 MAP DECORATION → RESPONSE
       ========================= */
    private List<HouseDecorationResponse> mapDecorations(List<HouseDecoration> decorations) {

        return decorations.stream()
                .map(deco -> {
                    ShopItem item = shopItemRepository.findById(deco.getItemId())
                            .orElseThrow();

                    return HouseDecorationResponse.builder()
                            .decorationId(deco.getId())
                            .itemId(item.getId())
                            .itemId(item.getId())
                            .name(item.getName())
                            .imageUrl(item.getImageUrl())
                            .posX(deco.getPosX())
                            .posY(deco.getPosY())
                            .zIndex(deco.getZIndex())
                            .build();
                })
                .toList();
    }
    public String getMyShareToken(User user) {
        House house = houseRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("User chưa có house"));
        return house.getShareToken();
    }

}

