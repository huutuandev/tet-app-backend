package com.tet.tet_app.service;

import com.tet.tet_app.dto.response.ProfileResponse;
import com.tet.tet_app.dto.request.ProfileUpdateRequest;
import com.tet.tet_app.entity.User;
import com.tet.tet_app.entity.Wallet;
import com.tet.tet_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileResponse getProfile(User user) {
        Wallet wallet = user.getWallet();
        int points = wallet != null ? wallet.getBalance() : 0;

        return new ProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.getFavoriteQuote(),
                points
        );
    }

    @Transactional
    public ProfileResponse updateProfile(User user, ProfileUpdateRequest request) {
        // Cập nhật thông tin
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }
        if (request.getFavoriteQuote() != null) {
            user.setFavoriteQuote(request.getFavoriteQuote());
        }
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isBlank()) {
           user.setAvatarUrl(request.getAvatarUrl());
        }

        User updatedUser = userRepository.save(user);

        Wallet wallet = updatedUser.getWallet();
        int points = wallet != null ? wallet.getBalance() : 0;

        return new ProfileResponse(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getFullName(),
                updatedUser.getAvatarUrl(),
                updatedUser.getFavoriteQuote(),
                points
        );
    }
}