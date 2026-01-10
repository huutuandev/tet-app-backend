package com.tet.tet_app.repository;

import com.tet.tet_app.entity.LuckyReward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LuckyRewardRepository extends JpaRepository<LuckyReward, Long> {
    List<LuckyReward> findByActiveTrue();

    long countByActiveTrue();
    Page<LuckyReward> findAll(Pageable pageable);

}
