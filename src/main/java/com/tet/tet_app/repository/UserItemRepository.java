package com.tet.tet_app.repository;

import com.tet.tet_app.dto.response.InventoryItemResponse;
import com.tet.tet_app.entity.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    boolean existsByUserIdAndItemId(Long userId, Long itemId);
    Optional<UserItem> findByUserIdAndItemId(Long userId, Long itemId);
    @Query("""
    SELECT new com.tet.tet_app.dto.response.InventoryItemResponse(
        item.id,
        item.name,
        item.imageUrl,
        ui.quantity
    )
    FROM UserItem ui
    JOIN ShopItem item ON item.id = ui.itemId
    WHERE ui.userId = :userId
""")
    List<InventoryItemResponse> findInventoryByUser(@Param("userId") Long userId);

}
