package com.tet.tet_app.repository;

import com.tet.tet_app.entity.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    boolean existsByUserIdAndItemId(Long userId, Long itemId);
}
