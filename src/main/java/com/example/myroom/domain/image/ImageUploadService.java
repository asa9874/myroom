package com.example.myroom.domain.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

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
            
            // 이미지 리사이징
            byte[] resizedImageBytes = resizeImage(file, originalFileName);
            
            String fileName = System.currentTimeMillis() + "." + getFileExtension(originalFileName);
            String filePath = UPLOAD_DIR + fileName;
            Files.write(Paths.get(filePath), resizedImageBytes);
            return "http://localhost:8080/images/" + fileName;
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
    
    private byte[] resizeImage(MultipartFile originalFile, String fileName) throws IOException {
        BufferedImage originalImage = ImageIO.read(originalFile.getInputStream());
        if (originalImage == null) {
            throw new IOException("Invalid image file");
        }
        
        // 512x512로 리사이징
        BufferedImage resizedImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, 512, 512, null);
        g2d.dispose();
        
        // BufferedImage를 byte array로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = getFileExtension(fileName);
        if (formatName.isEmpty()) {
            formatName = "jpg";
        }
        ImageIO.write(resizedImage, formatName, baos);
        return baos.toByteArray();
    }
}