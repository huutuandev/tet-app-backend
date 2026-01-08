package com.tet.tet_app.service;

import com.tet.tet_app.event.EmailVerificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationConsumer {

    private final EmailVerificationService emailVerificationService;

    @KafkaListener(
            topics = "email-verification-topic",
            groupId = "tet-email-verification-group"
    )
    public void consumeVerificationEvent(@Payload EmailVerificationEvent event) {
        log.info("📩 Nhận event xác thực email: {}", event.getEmail());

        try {
            emailVerificationService.sendVerificationEmail(
                    event.getEmail(),
                    event.getVerificationCode(),
                    event.getFullName()
            );
            log.info("✅ Gửi email xác thực thành công: {}", event.getEmail());
        } catch (Exception e) {
            log.error("❌ Lỗi gửi email xác thực", e);
        }
    }
}
