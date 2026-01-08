package com.tet.tet_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;

    @Value("${app.email.verification.expiration-minutes:15}")
    private long expirationMinutes;

    @Value("${app.email.verification.code-length:6}")
    private int codeLength;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String VERIFICATION_PREFIX = "email:verify:";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /**
     * Tạo mã xác thực và lưu vào Redis
     */
    public String createVerificationCode(String email) {
        String code = generateRandomCode(codeLength);
        String key = VERIFICATION_PREFIX + email;

        // Lưu code vào Redis với thời gian hết hạn
        redisTemplate.opsForValue().set(key, code, expirationMinutes, TimeUnit.MINUTES);
        log.info("Đã tạo mã xác thực cho email: {}", email);

        return code;
    }

    /**
     * Gửi email xác thực
     */
    public void sendVerificationEmail(String email, String code, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Xác thực email đăng ký - Tết Fun App");

            String htmlContent = buildEmailContent(fullName, code);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Đã gửi email xác thực đến: {}", email);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email xác thực: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể gửi email xác thực: " + e.getMessage());
        }
    }

    /**
     * Xác thực mã code
     */
    public boolean verifyCode(String email, String code) {
        String key = VERIFICATION_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode != null && storedCode.equals(code)) {
            // Xóa code sau khi xác thực thành công
            redisTemplate.delete(key);
            log.info("Xác thực email thành công: {}", email);
            return true;
        }

        log.warn("Xác thực email thất bại: {}", email);
        return false;
    }

    /**
     * Kiểm tra email format có hợp lệ không
     */
    public boolean isValidEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        return pattern.matcher(email).matches();
    }

    /**
     * Kiểm tra code có tồn tại trong Redis không
     */
    public boolean isCodeExists(String email) {
        String key = VERIFICATION_PREFIX + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Xóa code verification
     */
    public void deleteVerificationCode(String email) {
        String key = VERIFICATION_PREFIX + email;
        redisTemplate.delete(key);
    }

    /**
     * Generate random code
     */
    private String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * Build email HTML content
     */
    private String buildEmailContent(String fullName, String code) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .code-box { background: white; border: 2px dashed #667eea; padding: 20px; text-align: center; margin: 20px 0; border-radius: 8px; }
                    .code { font-size: 32px; font-weight: bold; color: #667eea; letter-spacing: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Chào mừng đến với Tết Fun App!</h1>
                    </div>
                    <div class="content">
                        <h2>Xin chào %s,</h2>
                        <p>Cảm ơn bạn đã đăng ký tài khoản. Để hoàn tất quá trình đăng ký, vui lòng nhập mã xác thực dưới đây:</p>
                        <div class="code-box">
                            <div class="code">%s</div>
                        </div>
                        <p><strong>Lưu ý:</strong> Mã này sẽ hết hạn sau <strong>%d phút</strong>.</p>
                        <p>Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email này.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Tết Fun App. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, fullName, code, expirationMinutes);
    }
}