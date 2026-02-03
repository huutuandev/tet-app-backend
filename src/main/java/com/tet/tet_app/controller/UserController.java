package com.tet.tet_app.controller;

import com.tet.tet_app.dto.request.ChangePasswordRequest;
import com.tet.tet_app.dto.request.UserActiveRequest;
import com.tet.tet_app.dto.response.ApiResponse;
import com.tet.tet_app.dto.response.UserResponse;
import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        Page<UserResponse> users = userService.getAllUsers(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Lấy danh sách user thành công",
                        users
                )
        );
    }

    @PutMapping("/users/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserActive(
            @PathVariable Long id,
            @RequestBody UserActiveRequest request) {

        UserResponse response =
                userService.updateUserActive(id, request.getActive());

        String msg = request.getActive()
                ? "Đã kích hoạt user"
                : "Đã vô hiệu hoá user";

        return ResponseEntity.ok(
                new ApiResponse<>(true, msg, response)
        );
    }

    @PutMapping("/change-own-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> changePassword(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(
                currentUser.getUser().getId(),
                request
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Đổi mật khẩu admin thành công",
                        null
                )
        );
    }


}
