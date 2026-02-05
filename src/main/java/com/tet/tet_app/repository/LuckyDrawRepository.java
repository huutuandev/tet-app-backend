package com.tet.tet_app.repository;

import com.tet.tet_app.entity.LuckyDraw;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface LuckyDrawRepository extends JpaRepository<LuckyDraw, Long> {
    Optional<LuckyDraw> findByUserIdAndDrawDate(Long userId, LocalDate drawDate);
}
