package com.tet.tet_app.service;

import com.tet.tet_app.entity.User;
import com.tet.tet_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public String login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
        return jwtService.generateToken(userDetails);
    }

    public String generateJwtForUser(User user) {

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(user.getEmail());
        return jwtService.generateToken(userDetails);
    }
}