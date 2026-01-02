package com.tet.tet_app.config;

import com.tet.tet_app.entity.Role;
import com.tet.tet_app.entity.User;
import com.tet.tet_app.entity.Wallet;
import com.tet.tet_app.repository.RoleRepository;
import com.tet.tet_app.repository.UserRepository;
import com.tet.tet_app.repository.WalletRepository;
import com.tet.tet_app.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final WalletRepository walletRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub");
        String avatar = oAuth2User.getAttribute("picture");

        // 1️⃣ Tìm hoặc tạo user
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .fullName(name)
                            .googleId(googleId)
                            .avatarUrl(avatar)
                            .build();

                    // gán ROLE_USER
                    Role roleUser = roleRepository.findByName("ROLE_USER")
                            .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
                    newUser.getRoles().add(roleUser);

                    User savedUser = userRepository.save(newUser);

                    // tạo wallet
                    Wallet wallet = Wallet.builder()
                            .user(savedUser)
                            .balance(100)
                            .build();
                    walletRepository.save(wallet);

                    return savedUser;
                });

        // 2️⃣ Generate JWT
        String token = jwtService.generateTokenFromEmail(user.getEmail());

        // 3️⃣ Redirect về FE
        String redirectUrl = "http://localhost:3000/oauth2/success?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}
