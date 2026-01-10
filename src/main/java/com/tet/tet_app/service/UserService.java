package com.tet.tet_app.service;

import com.tet.tet_app.dto.response.UserResponse;
import com.tet.tet_app.event.EmailVerificationEvent;  // ← FIX IMPORT NÀY
import com.tet.tet_app.dto.response.AuthResponse;
import com.tet.tet_app.entity.Role;
import com.tet.tet_app.entity.User;
import com.tet.tet_app.entity.Wallet;
import com.tet.tet_app.redis.model.TempUser;
import com.tet.tet_app.redis.service.TempUserService;
import com.tet.tet_app.repository.RoleRepository;
import com.tet.tet_app.repository.UserRepository;
import com.tet.tet_app.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // ← THÊM IMPORT NÀY
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j  // ← THÊM ANNOTATION NÀY
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final EmailVerificationProducer emailVerificationProducer;
    private final TempUserService tempUserService;

    @Transactional
    public void registerUser(String email, String password, String fullName) {

        if (!emailVerificationService.isValidEmailFormat(email)) {
            throw new RuntimeException("Email không hợp lệ!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã tồn tại!");
        }

        TempUser tempUser = TempUser.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .fullName(fullName)
                .build();

        // Lưu user tạm 15 phút
        tempUserService.saveTempUser(tempUser, 15);

        String code = emailVerificationService.createVerificationCode(email);

        EmailVerificationEvent event = new EmailVerificationEvent(
                email,
                code,
                fullName,
                null
        );
        emailVerificationProducer.sendVerificationEvent(event);

        log.info("Đã lưu user tạm trong Redis: {}", email);
    }


    @Transactional
    public User createUserFromTemp(TempUser temp) {

        User user = User.builder()
                .email(temp.getEmail())
                .passwordHash(temp.getPasswordHash())
                .fullName(temp.getFullName())
                .isActive(true)
                .roles(new HashSet<>())
                .build();

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));

        user.getRoles().add(role);
        user = userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(100)
                .build();
        walletRepository.save(wallet);

        return user;
    }


    @Transactional
    public User registerOrGetGoogleUser(String googleId, String email, String fullName, String avatarUrl) {
        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> userRepository.findByEmail(email).orElse(null));

        if (user == null) {
            user = User.builder()
                    .email(email)
                    .fullName(fullName)
                    .googleId(googleId)
                    .avatarUrl(avatarUrl)
                    .isActive(true)  // ← Google user tự động active
                    .roles(new HashSet<>())
                    .build();

            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Role ROLE_USER không tồn tại"));
            user.getRoles().add(userRole);

            user = userRepository.save(user);

            Wallet wallet = Wallet.builder()
                    .user(user)
                    .balance(100)
                    .build();
            walletRepository.save(wallet);

            return user;
        }

        boolean needUpdate = false;

        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
            needUpdate = true;
        }

        if (avatarUrl != null && !avatarUrl.equals(user.getAvatarUrl())) {
            user.setAvatarUrl(avatarUrl);
            needUpdate = true;
        }

        if (needUpdate) {
            user = userRepository.save(user);
        }

        return user;
    }

    //ADMIN
    public Page<UserResponse> getAllUsers(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public UserResponse updateUserActive(Long userId, Boolean active) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        user.setIsActive(active);

        User saved = userRepository.save(user);

        return mapToResponse(saved);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .enabled(user.getIsActive())
                .roles(
                        user.getRoles()
                                .stream()
                                .map(Role::getName) // ✅ map từ Role → String
                                .toList()
                )
                .build();
    }

}