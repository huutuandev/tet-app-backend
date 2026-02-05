package com.tet.tet_app.repository;


import com.tet.tet_app.entity.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {

    Page<Wish> findByReceiverId(Long receiverId, Pageable pageable);

    Page<Wish> findBySenderId(Long senderId, Pageable pageable);

    Page<Wish> findByIsPrivateFalse(Pageable pageable);

    Optional<Wish> findByShareToken(String shareToken);

    int countBySenderIdAndCreatedAtBetween(
            Long senderId,
            LocalDateTime start,
            LocalDateTime end
    );


}
