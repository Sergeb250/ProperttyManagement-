package com.btech.propertymgt.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "property_videos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String videoUrl;

    private String fileName;
    private String contentType;
    private String title;
    private String platform; // e.g. "S3_UPLOAD", "YOUTUBE", "VIMEO"

    // Specifically for when it's an external URL instead of a direct file upload
    private String externalEmbedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();
}
