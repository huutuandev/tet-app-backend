package com.tet.tet_app.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "house_decorations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseDecoration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "pos_x")
    private int posX = 0;

    @Column(name = "pos_y")
    private int posY = 0;

    @Column(name = "z_index")
    private int zIndex = 0;
}
