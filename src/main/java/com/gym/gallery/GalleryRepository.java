package com.gym.gallery;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GalleryRepository extends JpaRepository<GalleryItem, UUID> {
    List<GalleryItem> findByVisibleTrueAndDeletedFalseOrderBySortOrderAsc();
    List<GalleryItem> findAllByDeletedFalseOrderBySortOrderAsc();
}
