package com.gym.gallery;

import com.gym.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "gallery_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GalleryItem extends BaseEntity {

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private GalleryItemType type;

    private String caption;

    private String category; // e.g. "Equipment", "Events", "Transformations" - manager-defined free text

    @lombok.Builder.Default
    private boolean visible = true;

    @lombok.Builder.Default
    private int sortOrder = 0;
}
