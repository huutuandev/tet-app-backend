package com.tet.tet_app.service;

import com.tet.tet_app.dto.response.AuthResponse;
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
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse registerUser(String email, String password, String fullName) {
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

        String jwt = authService.generateJwtForUser(user);

        AuthResponse authResponse = AuthResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .token(jwt)
                .avatarUrl(user.getAvatarUrl())
                .build();
        return authResponse;
    }

    @Transactional
    public User registerOrGetGoogleUser(String googleId, String email, String fullName, String avatarUrl) {
        // Ưu tiên tìm theo googleId (nếu có), sau đó mới tìm theo email
        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> userRepository.findByEmail(email).orElse(null));

        // Nếu chưa tồn tại user nào → tạo mới
        if (user == null) {
            user = User.builder()
                    .email(email)
                    .fullName(fullName)
                    .googleId(googleId)
                    .avatarUrl(avatarUrl)  // có thể null nếu không truyền
                    .roles(new HashSet<>())
                    .build();

            // Gán ROLE_USER
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Role ROLE_USER không tồn tại"));
            user.getRoles().add(userRole);

            user = userRepository.save(user);

            // Tạo wallet với số dư khởi tạo 100
            Wallet wallet = Wallet.builder()
                    .user(user)
                    .balance(100)
                    .build();
            walletRepository.save(wallet);

            return user;
        }

        // Nếu user đã tồn tại
        boolean needUpdate = false;

        // Trường hợp user đăng nhập bằng email/password trước đó → liên kết với Google
        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
            needUpdate = true;
        }

        // Cập nhật avatar nếu có cung cấp và khác với hiện tại (tránh update không cần thiết)
        if (avatarUrl != null && !avatarUrl.equals(user.getAvatarUrl())) {
            user.setAvatarUrl(avatarUrl);
            needUpdate = true;
        }

        // Chỉ save nếu có thay đổi
        if (needUpdate) {
            user = userRepository.save(user);
        }

        // Wallet luôn được tạo khi user mới, nên ở đây không cần tạo lại
        // (nếu muốn đảm bảo chắc chắn có wallet, có thể thêm kiểm tra nhưng thường không cần)

        return user;
    }
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}