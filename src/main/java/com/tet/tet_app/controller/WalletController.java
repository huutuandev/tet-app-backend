package com.tet.tet_app.controller;

import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.dto.response.WalletTransactionResponse;
import com.tet.tet_app.entity.User;
import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<WalletTransactionResponse> data =
                walletService.getTransactions(currentUser.getUser(), page, size);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Lịch sử giao dịch ví",
                        data
                )
        );
    }


    @GetMapping("/transactions/{id}")
    public ResponseEntity<?> getTransactionById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser){

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Lịch sử giao dịch ví chi tiết",
                        walletService.getTransactionById(id, currentUser.getUser())
                )
        );
    }

}
