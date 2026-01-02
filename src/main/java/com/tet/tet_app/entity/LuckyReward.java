package com.tet.tet_app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lucky_rewards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LuckyReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "reward_type", nullable = false, length = 50)
    private String rewardType; // message, points, sticker, avatar

    @Column(name = "value")
    private int value = 0;

    private String message;
}