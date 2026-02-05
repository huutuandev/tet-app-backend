package com.tet.tet_app.repository;

import com.tet.tet_app.entity.Gift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftRepository extends JpaRepository<Gift, Long> {

    List<Gift> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    List<Gift> findBySenderIdOrderByCreatedAtDesc(Long senderId);

}
