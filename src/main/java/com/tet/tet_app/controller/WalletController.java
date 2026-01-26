package com.tet.tet_app.controller;

import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.dto.response.WalletTransactionResponse;
import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<WalletTransactionResponse> data =
                walletService.getTransactions(currentUser.getUser());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Lịch sử giao dịch ví",
                        data
                )
        );
    }
}
