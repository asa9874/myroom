package com.example.myroom.domain.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

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

    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    public String uploadImage(MultipartFile file) throws IOException {
        return uploadResizedPng(file, 512, "images/thumbnails/");
    }

    public String uploadProfileImage(MultipartFile file) throws IOException {
        return uploadResizedPng(file, 512, "images/profile/");
    }

    public Model3DImageUrls uploadModel3DImages(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Image file is empty");
        }
        validateImageFile(file);

        try {
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null) {
                throw new IOException("Invalid file name");
            }

            byte[] thumbnailImageBytes = processImageFor3D(file, 512);
            byte[] trainingImageBytes = processImageFor3D(file, 1024);

            long timestamp = System.currentTimeMillis();
            String thumbnailKey = "images/thumbnails/" + timestamp + "_512.png";
            String trainingKey = "images/training/" + timestamp + "_1024.png";

            String thumbnailUrl = uploadPngBytes(thumbnailKey, thumbnailImageBytes);
            String trainingImageUrl = uploadPngBytes(trainingKey, trainingImageBytes);

            return new Model3DImageUrls(thumbnailUrl, trainingImageUrl);
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new IOException("Image Upload Fail", e);
        }
    }

    private String uploadPngBytes(String key, byte[] imageBytes) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("image/png")
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }

    private String uploadResizedPng(MultipartFile file, int canvasSize, String directory) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Image file is empty");
        }
        validateImageFile(file);

        try {
            byte[] imageBytes = processImageFor3D(file, canvasSize);
            String key = directory + System.currentTimeMillis() + "_" + UUID.randomUUID() + "_" + canvasSize + ".png";
            return uploadPngBytes(key, imageBytes);
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new IOException("Image Upload Fail", e);
        }
    }

    private void validateImageFile(MultipartFile file) {
        // 1. MIME 타입 화이트리스트 검증 (JPG, PNG만 허용)
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다. (JPG, PNG만 가능)");
        }

        // 2. AVIF 매직 바이트 검사 - 확장자 위조 대응 (bytes 4-7: "ftyp", bytes 8-11: "avif"/"avis")
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[12];
            if (is.read(header) >= 12) {
                String ftyp = new String(header, 4, 4, StandardCharsets.ISO_8859_1);
                String brand = new String(header, 8, 4, StandardCharsets.ISO_8859_1);
                if ("ftyp".equals(ftyp) && (brand.startsWith("avif") || brand.startsWith("avis"))) {
                    throw new IllegalArgumentException("AVIF 형식은 지원하지 않습니다. JPG 또는 PNG를 사용하세요.");
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("파일 검증 중 오류가 발생했습니다.", e);
        }
    }

    private byte[] processImageFor3D(MultipartFile originalFile, int canvasSize) throws IOException {
        BufferedImage originalImage = ImageIO.read(originalFile.getInputStream());
        if (originalImage == null) {
            throw new IOException("Invalid image file");
        }
        
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        // 종횡비 계산
        double aspectRatio = (double) originalWidth / originalHeight;
        
        // 종횡비를 유지하면서 canvasSize x canvasSize에 맞는 크기 계산
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

