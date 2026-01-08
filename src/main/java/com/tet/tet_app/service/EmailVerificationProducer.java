package com.tet.tet_app.service;

import com.tet.tet_app.event.EmailVerificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationProducer {

    private final KafkaTemplate<String, EmailVerificationEvent> kafkaTemplate;
    private static final String TOPIC = "email-verification-topic";

    public void sendVerificationEvent(EmailVerificationEvent event) {
        kafkaTemplate.send(TOPIC, event);
        log.info("📤 Đã đẩy event xác thực email: {}", event.getEmail());
    }
}
