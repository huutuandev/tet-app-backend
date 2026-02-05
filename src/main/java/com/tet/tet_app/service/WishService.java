package com.tet.tet_app.service;

import com.tet.tet_app.dto.request.WishCreateRequest;
import com.tet.tet_app.dto.request.WishUpdateRequest;
import com.tet.tet_app.dto.response.WishResponse;
import com.tet.tet_app.entity.User;
import com.tet.tet_app.entity.Wish;
import com.tet.tet_app.repository.UserRepository;
import com.tet.tet_app.repository.WishRepository;
import com.tet.tet_app.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WishService {

    private final WishRepository wishRepository;
    private final UserRepository userRepository;

    // ✅ CREATE
    public WishResponse createWish(
            CustomUserDetails currentUser,
            WishCreateRequest request
    ) {
        Long userId = currentUser.getUser().getId();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        int createdToday = wishRepository
                .countBySenderIdAndCreatedAtBetween(
                        userId,
                        startOfDay,
                        endOfDay
                );

        if (createdToday >= 3) {
            throw new RuntimeException("Bạn chỉ được tạo tối đa 3 thiệp mỗi ngày 🎋");
        }

        Wish wish = Wish.builder()
                .senderId(userId)
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .isPrivate(request.getIsPrivate())
                .shareToken(
                        request.getEnableShare()
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
    public WishResponse updateWish(
            Long wishId,
            Long currentUserId,
            WishUpdateRequest request
    ) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new RuntimeException("Wish not found"));

        if (!wish.getSenderId().equals(currentUserId)) {
            throw new RuntimeException("No permission");
        }

        // 📝 Update content
        if (request.getContent() != null) {
            wish.setContent(request.getContent());
        }

        // 🔐 Update private
        if (request.getIsPrivate() != null) {
            wish.setPrivate(request.getIsPrivate());

            // Nếu chuyển sang private → tắt share
            if (request.getIsPrivate()) {
                wish.setShareToken(null);
            }
        }

        // 🔁 Update share
        if (request.getEnableShare() != null) {
            if (request.getEnableShare()) {
                if (wish.isPrivate()) {
                    throw new RuntimeException("Private wish cannot be shared");
                }
                if (wish.getShareToken() == null) {
                    wish.setShareToken(UUID.randomUUID().toString());
                }
            } else {
                wish.setShareToken(null);
            }
        }

        return toResponse(wishRepository.save(wish));
    }


    // 🔗 SHARE
    public WishResponse getWishByShareToken(String token) {

        // ❌ Token rỗng / null
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Share token is empty");
        }

        Wish wish = wishRepository.findByShareToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid share token"));

        // 🔐 Không cho share nếu là private
        if (wish.isPrivate()) {
            throw new RuntimeException("This wish is private");
        }

        // ❌ Token bị revoke
        if (wish.getShareToken() == null) {
            throw new RuntimeException("Share link is disabled");
        }

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

    public String getSenderName(Long senderId) {
        User user = userRepository.findById(senderId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người gửi"));
        return user.getFullName();
    }


}
