package com.btech.propertymgt.demo.services;

import com.btech.propertymgt.demo.models.Property;
import com.btech.propertymgt.demo.models.PropertyImage;
import com.btech.propertymgt.demo.models.PropertyVideo;
import com.btech.propertymgt.demo.models.Room;
import com.btech.propertymgt.demo.models.RoomImage;
import com.btech.propertymgt.demo.repositories.PropertyImageRepository;
import com.btech.propertymgt.demo.repositories.PropertyVideoRepository;
import com.btech.propertymgt.demo.repositories.RoomImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final PropertyImageRepository propertyImageRepository;
    private final PropertyVideoRepository propertyVideoRepository;
    private final RoomImageRepository roomImageRepository;
    private final S3Client s3Client;

    @Value("${supabase.s3.bucket}")
    private String bucketName;

    // Use the Supabase instance URL prefix for public objects
    private final String SUPABASE_PUBLIC_URL_PREFIX = "https://yojaqlqoeachnxyoryas.supabase.co/storage/v1/object/public/";

    public PropertyImage uploadPropertyImage(Property property, MultipartFile file, String category, boolean isCover) {
        String fileName = generateFileName(file.getOriginalFilename());
        String objectKey = "properties/" + property.getPropertyCode() + "/" + fileName;

        // Upload physical file
        uploadToS3(file, objectKey);

        // Save metadata
        PropertyImage image = new PropertyImage();
        image.setProperty(property);
        image.setFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setCategory(category);
        image.setCoverImage(isCover);
        image.setImageUrl(SUPABASE_PUBLIC_URL_PREFIX + bucketName + "/" + objectKey);

        return propertyImageRepository.save(image);
    }

    public RoomImage uploadRoomImage(Room room, MultipartFile file, String title, boolean isCover) {
        String fileName = generateFileName(file.getOriginalFilename());
        String objectKey = "rooms/" + room.getRoomCode() + "/" + fileName;

        // Upload physical file
        uploadToS3(file, objectKey);

        // Save metadata
        RoomImage image = new RoomImage();
        image.setRoom(room);
        image.setFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setTitle(title);
        image.setCoverImage(isCover);
        image.setImageUrl(SUPABASE_PUBLIC_URL_PREFIX + bucketName + "/" + objectKey);

        return roomImageRepository.save(image);
    }

    public PropertyVideo uploadPropertyVideo(Property property, MultipartFile file, String title, String platform,
            String externalEmbedId) {
        PropertyVideo video = new PropertyVideo();
        video.setProperty(property);
        video.setTitle(title);
        video.setPlatform(platform);
        video.setExternalEmbedId(externalEmbedId);

        if (file != null && !file.isEmpty()) {
            String fileName = generateFileName(file.getOriginalFilename());
            String objectKey = "properties/" + property.getPropertyCode() + "/videos/" + fileName;

            // Upload physical video file
            uploadToS3(file, objectKey);

            video.setFileName(file.getOriginalFilename());
            video.setContentType(file.getContentType());
            video.setVideoUrl(SUPABASE_PUBLIC_URL_PREFIX + bucketName + "/" + objectKey);
        } else if (externalEmbedId != null) {
            video.setVideoUrl("EXTERNAL_LINK"); // handled by frontend embed component usually
        }

        return propertyVideoRepository.save(video);
    }

    private void uploadToS3(MultipartFile file, String objectKey) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("Successfully uploaded file to S3: {}/{}", bucketName, objectKey);
        } catch (IOException e) {
            log.error("Failed to read multipart file during S3 upload", e);
            throw new RuntimeException("Failed to read file for storage upload", e);
        } catch (Exception e) {
            log.error("S3 upload failed for key: {}", objectKey, e);
            throw new RuntimeException("Cloud storage upload failed", e);
        }
    }

    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}
