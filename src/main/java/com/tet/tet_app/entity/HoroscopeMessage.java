package com.tet.tet_app.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "horoscope_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoroscopeMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private String message;
}