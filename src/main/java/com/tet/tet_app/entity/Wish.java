package com.tet.tet_app.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(nullable = false)
    private String content;

    @JsonProperty("isPrivate")
    @Column(name = "is_private")
    private boolean isPrivate;

    @Column(name = "share_token", unique = true, length = 60)
    private String shareToken;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}