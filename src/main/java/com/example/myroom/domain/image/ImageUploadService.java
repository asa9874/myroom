package com.example.myroom.domain.image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ImageUploadService {
    private static final String UPLOAD_DIR = "C:/uploads/";

    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Image file is empty");
        }
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null) {
                throw new IOException("Invalid file name");
            }
            String fileName = System.currentTimeMillis() + "." + getFileExtension(originalFileName);
            String filePath = UPLOAD_DIR + fileName;
            file.transferTo(new File(filePath));
            return "/images/" + fileName;
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new IOException("Image Upload Fail", e);
        }
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            return fileName.substring(index + 1);
        }
        return "";
    }
}