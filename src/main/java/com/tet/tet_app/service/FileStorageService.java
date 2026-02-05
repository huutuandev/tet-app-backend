package com.tet.tet_app.service;

import com.tet.tet_app.config.FileStorageConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final FileStorageConfig fileStorageConfig;

    /**
     * Xóa file cũ dựa trên URL công khai (public URL)
     * Chỉ xóa nếu file thuộc hệ thống quản lý (bắt đầu bằng publicUrl và chứa /api/images/)
     *
     * @param oldFileUrl URL đầy đủ của file cũ (ví dụ: https://api.tet-fun-app.online/api/images/abc123.jpg)
     */
    public void deleteOldFile(String oldFileUrl) {
        if (oldFileUrl == null || oldFileUrl.isBlank()) {
            return;
        }

        // Kiểm tra xem có phải file do hệ thống quản lý không
        if (!fileStorageConfig.isSystemManagedImage(oldFileUrl)) {
            log.debug("Bỏ qua xóa file không thuộc hệ thống: {}", oldFileUrl);
            return;
        }

        try {
            String fileName = fileStorageConfig.extractFileNameFromUrl(oldFileUrl);
            if (fileName == null || fileName.isBlank()) {
                log.warn("Không thể trích xuất tên file từ URL: {}", oldFileUrl);
                return;
            }

            Path uploadPath = Paths.get(fileStorageConfig.getUploadDir());
            Path filePath = uploadPath.resolve(fileName).normalize();

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Đã xóa file cũ thành công: {}", filePath);
            } else {
                log.warn("File cũ không tồn tại để xóa: {}", filePath);
            }
        } catch (Exception e) {
            log.warn("Không thể xóa file cũ: {} - Lỗi: {}", oldFileUrl, e.getMessage(), e);
        }
    }

    /**
     * Phiên bản overload: Xóa nhiều file cùng lúc (danh sách URL)
     */
    public void deleteOldFiles(Iterable<String> oldFileUrls) {
        if (oldFileUrls == null) {
            return;
        }
        oldFileUrls.forEach(this::deleteOldFile);
    }

    /**
     * Xóa file theo tên file trực tiếp (không cần URL, dùng khi đã biết fileName)
     */
    public void deleteFileByName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }

        try {
            Path uploadPath = Paths.get(fileStorageConfig.getUploadDir());
            Path filePath = uploadPath.resolve(fileName).normalize();

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Đã xóa file theo tên: {}", filePath);
            } else {
                log.warn("File không tồn tại để xóa: {}", filePath);
            }
        } catch (Exception e) {
            log.warn("Không thể xóa file theo tên: {} - Lỗi: {}", fileName, e.getMessage(), e);
        }
    }
}