package com.tet.tet_app.repository;

import com.tet.tet_app.entity.HoroscopeMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HoroscopeMessageRepository
        extends JpaRepository<HoroscopeMessage, Long> {

    List<HoroscopeMessage> findByCategory(String category);
}
