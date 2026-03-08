package com.btech.propertymgt.demo.repositories;

import com.btech.propertymgt.demo.models.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
    List<RoomImage> findByRoomIdOrderByDisplayOrderAsc(Long roomId);
}
