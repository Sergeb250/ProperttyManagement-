package com.btech.propertymgt.demo.repositories;

import com.btech.propertymgt.demo.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByParentId(Long parentId);

    List<Location> findByType(Location.LocationType type);
}
