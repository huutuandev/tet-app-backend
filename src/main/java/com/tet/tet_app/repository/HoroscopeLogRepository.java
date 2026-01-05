package com.tet.tet_app.repository;

import com.tet.tet_app.entity.HoroscopeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface HoroscopeLogRepository
        extends JpaRepository<HoroscopeLog, Long> {

    Optional<HoroscopeLog> findByUserIdAndViewDate(
            Long userId,
            LocalDate viewDate
    );
}
