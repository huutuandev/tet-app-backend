package com.tet.tet_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@Getter
public class FileStorageConfig {

    private static final Logger log = LoggerFactory.getLogger(FileStorageConfig.class);

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.public-url}")
    private String publicUrl;

    // Để debug khi khởi động ứng dụng
    @PostConstruct
    public void logConfig() {
        log.info("File Storage Config loaded:");
        log.info("  → Upload directory (physical path): {}", uploadDir);
        log.info("  → Public base URL: {}", publicUrl);
    }

    /**
     * Lấy tên file từ URL đầy đủ (ví dụ: https://.../api/images/abc123.jpg → abc123.jpg)
     */
    public String extractFileNameFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        // Kiểm tra xem URL có bắt đầu bằng publicUrl không
        if (!imageUrl.startsWith(publicUrl)) {
            log.warn("URL không thuộc hệ thống quản lý: {}", imageUrl);
            return null;
        }

        // Cắt phần sau publicUrl + "/api/images/"
        String pathPart = "/api/images/";
        int index = imageUrl.indexOf(pathPart);
        if (index == -1) {
            return null;
        }

        return imageUrl.substring(index + pathPart.length());
    }

    /**
     * Kiểm tra xem URL có phải là ảnh do hệ thống upload không
     */
    public boolean isSystemManagedImage(String imageUrl) {
        return imageUrl != null &&
                !imageUrl.isBlank() &&
                imageUrl.startsWith(publicUrl) &&
                imageUrl.contains("/api/images/");
    }
}