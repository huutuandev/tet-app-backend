package com.tet.tet_app.controller;

import com.tet.tet_app.dto.request.LoginRequest;
import com.tet.tet_app.dto.request.RegisterRequest;
import com.tet.tet_app.dto.response.AuthResponse;
import com.tet.tet_app.entity.User;
import com.tet.tet_app.repository.UserRepository;
import com.tet.tet_app.service.AuthService;
import com.tet.tet_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
            return ResponseEntity.ok(userService.registerUser(request.getEmail(), request.getPassword(), request.getFullName()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
            return ResponseEntity.ok(authService.login(request.getEmail(), request.getPassword()));
    }
}
