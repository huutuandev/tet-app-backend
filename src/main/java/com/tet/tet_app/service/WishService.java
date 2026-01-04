package com.tet.tet_app.service;

import com.tet.tet_app.dto.request.WishCreateRequest;
import com.tet.tet_app.dto.response.WishResponse;
import com.tet.tet_app.entity.Wish;
import com.tet.tet_app.repository.WishRepository;
import com.tet.tet_app.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WishService {

    private final WishRepository wishRepository;

    // ✅ CREATE
    public WishResponse createWish(
            CustomUserDetails currentUser,
            WishCreateRequest request
    ) {
        Wish wish = Wish.builder()
                .senderId(currentUser.getId())
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .isPrivate(request.isPrivate())
                .shareToken(
                        request.isEnableShare()
                                ? UUID.randomUUID().toString()
                                : null
                )
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(wishRepository.save(wish));
    }

    // 📤 SENT
    public Page<WishResponse> getSent(Long userId, Pageable pageable) {
        return wishRepository
                .findBySenderId(userId, pageable)
                .map(this::toResponse);
    }

    // 🔍 DETAIL
    public WishResponse getWishById(Long wishId, Long currentUserId) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new RuntimeException("Wish not found"));

        if (wish.isPrivate()
                && !wish.getSenderId().equals(currentUserId)
                && (wish.getReceiverId() == null
                || !wish.getReceiverId().equals(currentUserId))) {
            throw new RuntimeException("Access denied");
        }

        return toResponse(wish);
    }

    // 🔗 SHARE
    public WishResponse getWishByShareToken(String token) {
        Wish wish = wishRepository.findByShareToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid share token"));
        return toResponse(wish);
    }

    // 🗑 DELETE
    public void deleteWish(Long wishId, Long currentUserId) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new RuntimeException("Wish not found"));

        if (!wish.getSenderId().equals(currentUserId)) {
            throw new RuntimeException("No permission");
        }

        wishRepository.delete(wish);
    }

    // 🔄 MAPPER
    private WishResponse toResponse(Wish wish) {
        return WishResponse.builder()
                .id(wish.getId())
                .senderId(wish.getSenderId())
                .receiverId(wish.getReceiverId())
                .content(wish.getContent())
                .isPrivate(wish.isPrivate())
                .shareToken(wish.getShareToken())
                .createdAt(wish.getCreatedAt())
                .build();
    }
}
