package com.example.myroom.domain.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@Log4j2
@RequiredArgsConstructor
public class S3ImageUploadService {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Image file is empty");
        }

        try {
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null) {
                throw new IOException("Invalid file name");
            }

            // 이미지 리사이징
            byte[] resizedImageBytes = resizeImage(file, originalFileName);

            String fileName = System.currentTimeMillis() + "." + getFileExtension(originalFileName);
            String key = "images/" + fileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(resizedImageBytes));

            return "https://" + bucketName + ".s3.amazonaws.com/" + key;
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new IOException("Image Upload Fail", e);
        }
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return index > 0 ? fileName.substring(index + 1) : "";
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

