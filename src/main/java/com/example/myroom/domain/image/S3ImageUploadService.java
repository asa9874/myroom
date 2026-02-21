package com.example.myroom.domain.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

            // 3D 모델링용 이미지 전처리 (종횡비 유지, 패딩 추가, PNG 투명 배경)
            byte[] processedImageBytes = processImageFor3D(file, originalFileName);

            String fileName = System.currentTimeMillis() + ".png"; // 항상 PNG로 저장
            String key = "images/" + fileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("image/png")
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(processedImageBytes));

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
    
    private byte[] processImageFor3D(MultipartFile originalFile, String fileName) throws IOException {
        BufferedImage originalImage = ImageIO.read(originalFile.getInputStream());
        if (originalImage == null) {
            throw new IOException("Invalid image file");
        }
        
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        // 종횡비 계산
        double aspectRatio = (double) originalWidth / originalHeight;
        
        // 512x512 캔버스 전체 영역 사용 (패딩 없음)
        int canvasSize = 512;
        
        // 종횡비를 유지하면서 512x512에 맞는 크기 계산
        int newWidth, newHeight;
        if (aspectRatio > 1.0) { // 가로가 더 긴 경우
            newWidth = canvasSize;
            newHeight = (int) (canvasSize / aspectRatio);
        } else { // 세로가 더 길거나 정사각형인 경우
            newHeight = canvasSize;
            newWidth = (int) (canvasSize * aspectRatio);
        }
        
        // PNG 투명 배경 캔버스 생성
        BufferedImage processedImage = new BufferedImage(canvasSize, canvasSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = processedImage.createGraphics();
        
        // 고품질 렌더링 설정
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 투명 배경 (기본값이므로 별도 설정 불필요)
        
        // 이미지를 중앙에 배치
        int x = (canvasSize - newWidth) / 2;
        int y = (canvasSize - newHeight) / 2;
        
        g2d.drawImage(originalImage, x, y, newWidth, newHeight, null);
        g2d.dispose();
        
        // PNG로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(processedImage, "png", baos);
        return baos.toByteArray();
    }
}

