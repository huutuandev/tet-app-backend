package com.tet.tet_app.service;

import com.tet.tet_app.dto.response.LeaderboardResponse;
import com.tet.tet_app.dto.response.WalletTransactionResponse;
import com.tet.tet_app.entity.User;
import com.tet.tet_app.entity.WalletTransaction;
import com.tet.tet_app.repository.WalletRepository;
import com.tet.tet_app.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public List<LeaderboardResponse> getTop10() {
        return walletRepository.findTopLeaderboard(PageRequest.of(0, 10));
    }

    public Page<WalletTransactionResponse> getTransactions(
            User user,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<WalletTransaction> transactions =
                transactionRepository.findByUserId(user.getId(), pageable);

        return transactions.map(tx ->
                WalletTransactionResponse.builder()
                        .id(tx.getId())
                        .userId(tx.getUserId())
                        .amount(tx.getAmount())
                        .type(tx.getType())
                        .description(tx.getDescription())
                        .createdAt(tx.getCreatedAt())
                        .build()
        );
    }


    public WalletTransactionResponse getTransactionById(Long id, User user){
        WalletTransaction walletTransaction = transactionRepository
                .findByIdAndUserId(id, user.getId()).orElseThrow(()->
                        new RuntimeException("Không tìm thấy gaio dịch"));
        return WalletTransactionResponse.builder()
                .id(walletTransaction.getId())
                .userId(walletTransaction.getUserId())
                .amount(walletTransaction.getAmount())
                .type(walletTransaction.getType())
                .description(walletTransaction.getDescription())
                .createdAt(walletTransaction.getCreatedAt())
                .build();
    }
}

