package com.tet.tet_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "houses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "share_token", unique = true, nullable = false)
    private String shareToken;

    @OneToMany(
            mappedBy = "house",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<HouseDecoration> decorations = new ArrayList<>();
}


