package com.btech.propertymgt.demo.repositories;

import com.btech.propertymgt.demo.models.PropertyVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyVideoRepository extends JpaRepository<PropertyVideo, Long> {
}
