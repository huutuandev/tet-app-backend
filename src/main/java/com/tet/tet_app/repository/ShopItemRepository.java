package com.tet.tet_app.repository;

import com.tet.tet_app.entity.ShopItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {
    Page<ShopItem> findByActiveTrue(Pageable pageable);
    Optional<ShopItem> findByIdAndActiveTrue(Long id);
    // ADMIN
    Page<ShopItem> findAll(Pageable pageable);
}
