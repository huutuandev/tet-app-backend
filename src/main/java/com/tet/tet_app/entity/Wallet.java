package com.tet.tet_app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int balance = 100;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}