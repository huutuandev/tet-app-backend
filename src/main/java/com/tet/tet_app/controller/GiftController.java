package com.tet.tet_app.controller;

import com.tet.tet_app.dto.request.GiftSendRequest;
import com.tet.tet_app.dto.response.GiftResponse;
import com.tet.tet_app.entity.Gift;
import com.tet.tet_app.security.user.CustomUserDetails;
import com.tet.tet_app.service.GiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/gifts")
@RequiredArgsConstructor
public class GiftController {

    private final GiftService giftService;

    @PostMapping("/send")
    public ResponseEntity<GiftResponse> sendGift(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody GiftSendRequest request) {

        return ResponseEntity.ok(
                giftService.sendGift(currentUser.getUser(), request)
        );
    }
}
