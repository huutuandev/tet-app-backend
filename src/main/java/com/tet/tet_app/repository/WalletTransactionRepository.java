package com.tet.tet_app.repository;

import com.tet.tet_app.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository
        extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
}
