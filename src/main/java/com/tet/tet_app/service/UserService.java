package com.tet.tet_app.service;

import com.tet.tet_app.entity.Role;
import com.tet.tet_app.entity.User;
import com.tet.tet_app.entity.Wallet;
import com.tet.tet_app.repository.RoleRepository;
import com.tet.tet_app.repository.UserRepository;
import com.tet.tet_app.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String email, String password, String fullName) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã tồn tại!");
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .fullName(fullName)
                .roles(new HashSet<>())
                .build();

        // Gán ROLE_USER
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));
        user.getRoles().add(userRole);

        user = userRepository.save(user);

        // Tạo wallet tự động
        Wallet wallet = Wallet.builder()
//                .userId(user.getId())
                .balance(100)
                .user(user)
                .build();
        walletRepository.save(wallet);

        return user;
    }

    @Transactional
    public User registerOrGetGoogleUser(String googleId, String email, String fullName) {
        // Check nếu user đã tồn tại qua google_id hoặc email
        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> userRepository.findByEmail(email).orElse(null));

        if (user == null) {
            user = User.builder()
                    .email(email)
                    .fullName(fullName)
                    .googleId(googleId)
                    .roles(new HashSet<>())
                    .build();

            // Gán ROLE_USER
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));
            user.getRoles().add(userRole);

            user = userRepository.save(user);

            // Tạo wallet
            Wallet wallet = Wallet.builder()
//                    .userId(user.getId())
                    .balance(100)
                    .user(user)
                    .build();
            walletRepository.save(wallet);
        } else if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
            userRepository.save(user);
        }

        return user;
    }
    @Transactional
    public User registerOrLoginGoogle(String googleId, String email, String fullName, String avatarUrl) {
        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> userRepository.findByEmail(email).orElse(null));

        if (user == null) {
            user = User.builder()
                    .email(email)
                    .fullName(fullName)
                    .googleId(googleId)
                    .avatarUrl(avatarUrl)
                    .roles(new HashSet<>())
                    .build();

            Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();
            user.getRoles().add(userRole);

            user = userRepository.save(user);

            Wallet wallet = Wallet.builder()
//                    .userId(user.getId())
                    .balance(100)
                    .user(user)
                    .build();
            walletRepository.save(wallet);
        }

        return user;
    }
}