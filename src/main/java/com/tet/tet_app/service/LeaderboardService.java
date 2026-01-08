package com.tet.tet_app.service;

import com.tet.tet_app.dto.response.LeaderboardResponse;
import com.tet.tet_app.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final WalletRepository walletRepository;

    public List<LeaderboardResponse> getTop10() {
        return walletRepository.findTopLeaderboard(PageRequest.of(0, 10));
    }
}

