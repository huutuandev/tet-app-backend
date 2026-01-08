package com.tet.tet_app.controller;

import com.tet.tet_app.entity.WalletTransaction;
import com.tet.tet_app.repository.WalletTransactionRepository;
import com.tet.tet_app.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletTransactionRepository transactionRepository;

    @GetMapping("/transactions")
    public List<WalletTransaction> getTransactions(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return transactionRepository
                .findByUserIdOrderByCreatedAtDesc(
                        currentUser.getUser().getId()
                );
    }
}
