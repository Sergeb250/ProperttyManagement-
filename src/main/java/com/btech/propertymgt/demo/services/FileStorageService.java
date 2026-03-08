package com.btech.propertymgt.demo.services;

import com.btech.propertymgt.demo.models.Property;
import com.btech.propertymgt.demo.models.PropertyImage;
import com.btech.propertymgt.demo.models.Room;
import com.btech.propertymgt.demo.models.RoomImage;
import com.btech.propertymgt.demo.repositories.PropertyImageRepository;
import com.btech.propertymgt.demo.repositories.RoomImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final PropertyImageRepository propertyImageRepository;
    private final RoomImageRepository roomImageRepository;

    // In a real application, replace this mock with S3/GCP/Blob upload logics.
    public PropertyImage uploadPropertyImage(Property property, String fileName, String contentType, String category,
            boolean isCover) {
        PropertyImage image = new PropertyImage();
        image.setProperty(property);
        image.setFileName(fileName);
        image.setContentType(contentType);
        image.setCategory(category);
        image.setCoverImage(isCover);
        image.setImageUrl("https://propertymgt.example.com/uploads/" + System.currentTimeMillis() + "_" + fileName);
        return propertyImageRepository.save(image);
    }

    public RoomImage uploadRoomImage(Room room, String fileName, String contentType, String title, boolean isCover) {
        RoomImage image = new RoomImage();
        image.setRoom(room);
        image.setFileName(fileName);
        image.setContentType(contentType);
        image.setTitle(title);
        image.setCoverImage(isCover);
        image.setImageUrl(
                "https://propertymgt.example.com/uploads/rooms/" + System.currentTimeMillis() + "_" + fileName);
        return roomImageRepository.save(image);
    }
}
