package com.tet.tet_app.service;

import com.tet.tet_app.dto.request.PlaceItemRequest;
import com.tet.tet_app.dto.response.HouseDecorationResponse;
import com.tet.tet_app.entity.HouseDecoration;
import com.tet.tet_app.entity.ShopItem;
import com.tet.tet_app.entity.User;
import com.tet.tet_app.entity.UserItem;
import com.tet.tet_app.repository.HouseDecorationRepository;
import com.tet.tet_app.repository.ShopItemRepository;
import com.tet.tet_app.repository.UserItemRepository;
import com.tet.tet_app.repository.UserRepository;
import com.tet.tet_app.until.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseService {

    private final HouseDecorationRepository houseDecorationRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;
    private final ShopItemRepository shopItemRepository;

    // 🏠 Nhà của mình
    public List<HouseDecorationResponse> getMyHouse(User user) {
        return houseDecorationRepository.findHouseByUser(user.getId());
    }

    // 👀 Xem nhà người khác (ID)
    public List<HouseDecorationResponse> getUserHouseById(Long userId) {
        return houseDecorationRepository.findHouseByUser(userId);
    }

    // ➕ ĐẶT ITEM VÀO NHÀ
    // ➕ ĐẶT ITEM VÀO NHÀ (và trừ inventory)
    @Transactional
    public void placeItem(User user, PlaceItemRequest request) {

        Long itemId = request.getItemId();

        // 1️⃣ Kiểm tra và trừ 1 quantity trong inventory
        UserItem userItem = userItemRepository.findByUserIdAndItemId(user.getId(), itemId)
                .orElseThrow(() -> new RuntimeException("Bạn chưa sở hữu vật phẩm này"));

        if (userItem.getQuantity() <= 0) {
            throw new RuntimeException("Không đủ số lượng vật phẩm trong kho");
        }

        userItem.setQuantity(userItem.getQuantity() - 1);
        userItemRepository.save(userItem);

//         Nếu quantity về 0, có thể xóa record (tùy thiết kế)
         if (userItem.getQuantity() == 0) {
             userItemRepository.delete(userItem);
         }

        // 2️⃣ Tạo bản ghi đặt trong nhà
        HouseDecoration decoration = HouseDecoration.builder()
                .userId(user.getId())
                .itemId(itemId)
                .posX(request.getPosX())
                .posY(request.getPosY())
                .zIndex(request.getZIndex())
                .build();

        houseDecorationRepository.save(decoration);
    }

    // ✏️ CẬP NHẬT VỊ TRÍ
    @Transactional
    public void updateDecoration(User user, Long decorationId,
                                 PlaceItemRequest request) {

        HouseDecoration decoration = houseDecorationRepository.findById(decorationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy decoration"));

        if (!decoration.getUserId().equals(user.getId())) {
            throw new RuntimeException("Không có quyền sửa nhà người khác");
        }

        decoration.setPosX(request.getPosX());
        decoration.setPosY(request.getPosY());
        decoration.setZIndex(request.getZIndex());
    }

    // ❌ GỠ ITEM KHỎI NHÀ
    @Transactional
    public void removeDecoration(User user, Long decorationId) {

        HouseDecoration decoration = houseDecorationRepository.findById(decorationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vật phẩm trong nhà"));

        if (!decoration.getUserId().equals(user.getId())) {
            throw new RuntimeException("Không có quyền sửa nhà người khác");
        }

        Long itemId = decoration.getItemId();

        // 1️⃣ Xóa khỏi nhà
        houseDecorationRepository.delete(decoration);

        // 2️⃣ Trả lại inventory (+1 quantity)
        userItemRepository.findByUserIdAndItemId(user.getId(), itemId)
                .ifPresentOrElse(
                        item -> {
                            item.setQuantity(item.getQuantity() + 1);
                            userItemRepository.save(item);
                        },
                        () -> {
                            // Trường hợp hiếm (fallback)
                            UserItem newItem = UserItem.builder()
                                    .userId(user.getId())
                                    .itemId(itemId)
                                    .quantity(1)
                                    .build();
                            userItemRepository.save(newItem);
                        }
                );
    }

    public List<HouseDecorationResponse> getUserHouseBySlug(String slug) {

        // 1️⃣ Tách userId từ slug
        Long userId = extractUserId(slug);

        // 2️⃣ Lấy user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // 3️⃣ (OPTIONAL) Check slug có đúng full_name không
        String expectedSlug = SlugUtil.toSlug(user.getFullName()) + "-" + userId;
        if (!expectedSlug.equals(slug)) {
            throw new RuntimeException("Link nhà không hợp lệ");
        }

        // 4️⃣ Lấy danh sách decoration
        return houseDecorationRepository.findHouseByUser(userId)
                .stream()
                .map(deco -> {
                    ShopItem item = shopItemRepository.findById(deco.getItemId())
                            .orElseThrow();

                    return HouseDecorationResponse.builder()
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

    private Long extractUserId(String slug) {
        try {
            String[] parts = slug.split("-");
            return Long.parseLong(parts[parts.length - 1]);
        } catch (Exception e) {
            throw new RuntimeException("Slug không hợp lệ");
        }
    }
}
