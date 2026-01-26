package com.tet.tet_app.entity;

import com.tet.tet_app.entity.enums.ShopItemCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shop_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShopItemCategory category;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private boolean active = true; // disable thay vì xóa cứng
}
