package com.tet.tet_app.repository;

import com.tet.tet_app.entity.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletTransactionRepository
        extends JpaRepository<WalletTransaction, Long> {

    Page<WalletTransaction> findByUserId(Long userId, Pageable pageable);


    Optional<WalletTransaction> findByIdAndUserId(Long id, Long userId);
}
