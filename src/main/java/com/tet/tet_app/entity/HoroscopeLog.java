package com.tet.tet_app.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "horoscope_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoroscopeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "view_date", nullable = false)
    private LocalDate viewDate;
}