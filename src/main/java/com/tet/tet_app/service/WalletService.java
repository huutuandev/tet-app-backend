package com.tet.tet_app.service;

import com.tet.tet_app.dto.response.LeaderboardResponse;
import com.tet.tet_app.dto.response.WalletTransactionResponse;
import com.tet.tet_app.entity.User;
import com.tet.tet_app.entity.WalletTransaction;
import com.tet.tet_app.repository.WalletRepository;
import com.tet.tet_app.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

    public List<WalletTransactionResponse> getTransactions(User user) {

        List<WalletTransaction> transactions =
                transactionRepository.findByUserIdOrderByCreatedAtDesc(
                        user.getId()
                );

        return transactions.stream()
                .map(tx -> WalletTransactionResponse.builder()
                        .id(tx.getId())
                        .userId(tx.getUserId())
                        .amount(tx.getAmount())
                        .type(tx.getType())
                        .description(tx.getDescription())
                        .createdAt(tx.getCreatedAt())
                        .build()
                )
                .toList();
    }

}

