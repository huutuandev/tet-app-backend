package com.tet.tet_app.repository;

import com.tet.tet_app.entity.House;
import com.tet.tet_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HouseRepository extends JpaRepository<House, Long> {
    Optional<House> findByShareToken(String shareToken);

    Optional<House> findByUser(User user);

    Optional<House> findByUserId(Long userId);

}
