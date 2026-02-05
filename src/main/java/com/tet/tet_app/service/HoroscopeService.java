package com.tet.tet_app.service;

import com.tet.tet_app.entity.HoroscopeLog;
import com.tet.tet_app.entity.HoroscopeMessage;
import com.tet.tet_app.entity.Wallet;
import com.tet.tet_app.entity.WalletTransaction;
import com.tet.tet_app.repository.HoroscopeLogRepository;
import com.tet.tet_app.repository.HoroscopeMessageRepository;
import com.tet.tet_app.repository.WalletRepository;
import com.tet.tet_app.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class HoroscopeService {

    private final HoroscopeMessageRepository messageRepo;
    private final HoroscopeLogRepository logRepo;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    private static final int HOROSCOPE_POINT = 5;

    public String viewTodayHoroscope(Long userId, String category) {

        LocalDate today = LocalDate.now();

        // 1️⃣ Check đã xem hôm nay chưa
        Optional<HoroscopeLog> logOpt =
                logRepo.findByUserIdAndViewDate(userId, today);

        boolean firstViewToday = logOpt.isEmpty();

        // 2️⃣ Lấy message cố định
        List<HoroscopeMessage> messages =
                messageRepo.findByCategory(category);

        if (messages.isEmpty()) {
            throw new RuntimeException("No horoscope message");
        }

        int index = Math.abs(
                Objects.hash(userId, today, category)
        ) % messages.size();

        String message = messages.get(index).getMessage();

        // 3️⃣ Nếu lần đầu → lưu log + cộng điểm
        if (firstViewToday) {

            // 3.1 Lưu log
            HoroscopeLog log = HoroscopeLog.builder()
                    .userId(userId)
                    .viewDate(today)
                    .build();

            logRepo.save(log);

            // 3.2 Cộng điểm
            Wallet wallet = walletRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Wallet not found"));

            wallet.setBalance(wallet.getBalance() + HOROSCOPE_POINT);
            walletRepository.save(wallet);

            // 3.3 Lưu lịch sử giao dịch
            WalletTransaction tx = WalletTransaction.builder()
                    .userId(userId)
                    .amount(HOROSCOPE_POINT)
                    .type("HOROSCOPE_REWARD")
                    .description("Reward for daily horoscope")
                    .createdAt(LocalDateTime.now())
                    .build();

            walletTransactionRepository.save(tx);
        }

        return message;
    }
}

