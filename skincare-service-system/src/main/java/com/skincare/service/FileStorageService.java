package com.skincare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        try {
            // Tạo tên file duy nhất
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Lưu file
            Path targetLocation = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Trả về đường dẫn tương đối của file
            return "/uploads/" + newFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file: " + ex.getMessage());
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl != null && fileUrl.startsWith("/uploads/")) {
                String filename = fileUrl.substring("/uploads/".length());
                Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
                
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Không thể xóa file: " + ex.getMessage());
        }
    }
} 