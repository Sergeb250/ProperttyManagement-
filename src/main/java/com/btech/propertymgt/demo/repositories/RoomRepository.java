package com.btech.propertymgt.demo.repositories;

import com.btech.propertymgt.demo.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomCode(String roomCode);

    List<Room> findByPropertyId(Long propertyId);

    List<Room> findByPropertyIdAndRoomStatus(Long propertyId, Room.RoomStatus status);
}
