package org.multimedia.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class FileStorageUtil {

    @Value("${client.participant.logoPath:C:/etswitch/uploads/logos/}")
    private String logoUploadLocation;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("png", "jpg", "jpeg", "svg", "webp");

    public String saveLogoFile(String bic, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Logo file cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "png";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            log.warn("Invalid logo file format rejected: {}", extension);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unsupported image format: " + extension + ". Allowed formats: " + ALLOWED_EXTENSIONS);
        }

        String sanitizedBic = bic != null ? bic.replaceAll("[^a-zA-Z0-9_-]", "") : "bank";
        String fileName = sanitizedBic + "_logo." + extension;

        String uploadDir = logoUploadLocation.endsWith("/") || logoUploadLocation.endsWith("\\")
                ? logoUploadLocation
                : logoUploadLocation + File.separator;

        try {
            Path targetDirectory = Paths.get(uploadDir);
            Files.createDirectories(targetDirectory);

            File targetFile = new File(uploadDir, fileName);
            file.transferTo(targetFile);

            log.info("Participant logo saved successfully for {}: {}", bic, targetFile.getAbsolutePath());
            return "/uploads/logos/" + fileName;
        } catch (IOException ex) {
            log.error("Failed to store logo file on disk for BIC {}: {}", bic, ex.getMessage(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store logo file: " + ex.getMessage());
        }
    }
}
