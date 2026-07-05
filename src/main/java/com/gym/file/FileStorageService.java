package com.gym.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Local disk storage for now (uploads/ folder served statically).
 * To move to AWS S3 or Cloudinary later: implement same method signatures against their SDK
 * and swap this @Service - no controller/service code elsewhere needs to change.
 */
@Service
@Slf4j
public class FileStorageService {

    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDir;

    public String store(MultipartFile file, String subFolder) {
        try {
            String extension = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + (extension.isBlank() ? "" : "." + extension);

            Path folderPath = Path.of(uploadDir, subFolder);
            Files.createDirectories(folderPath);

            Path target = folderPath.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + subFolder + "/" + filename; // served via WebMvcConfig resource handler
        } catch (IOException e) {
            log.error("File upload failed: {}", e.getMessage());
            throw new IllegalStateException("File upload failed: " + e.getMessage());
        }
    }

    public void delete(String relativeUrl) {
        try {
            if (relativeUrl == null || !relativeUrl.startsWith("/uploads/")) return;
            Path path = Path.of(uploadDir, relativeUrl.substring("/uploads/".length()));
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete file {}: {}", relativeUrl, e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
