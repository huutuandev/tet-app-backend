package com.tet.tet_app.service;

import com.tet.tet_app.dto.response.AuthResponse;
import com.tet.tet_app.entity.User;
import com.tet.tet_app.repository.UserRepository;
import com.tet.tet_app.security.jwt.JwtService;
import com.tet.tet_app.security.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthResponse login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
        String jwt = jwtService.generateToken(userDetails);
        User user = userRepository.findByEmail(email).orElseThrow(()
        -> new UsernameNotFoundException("Không tìm được tài khoản !"));
        return AuthResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .token(jwt)
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    public String generateJwtForUser(User user) {

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(user.getEmail());
        return jwtService.generateToken(userDetails);
    }
}