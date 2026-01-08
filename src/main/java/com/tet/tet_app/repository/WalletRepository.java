package com.tet.tet_app.repository;

import com.tet.tet_app.dto.response.LeaderboardResponse;
import com.tet.tet_app.entity.Wallet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);
    @Query("""
        SELECT new com.tet.tet_app.dto.response.LeaderboardResponse(
            u.id,
            u.fullName,
            u.avatarUrl,
            w.balance
        )
        FROM Wallet w
        JOIN w.user u
        ORDER BY w.balance DESC
    """)
    List<LeaderboardResponse> findTopLeaderboard(Pageable pageable);
}
