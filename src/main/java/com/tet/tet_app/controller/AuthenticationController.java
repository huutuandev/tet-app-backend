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
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(userService.registerUser(request.getEmail(), request.getPassword(), request.getFullName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request.getEmail(), request.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
