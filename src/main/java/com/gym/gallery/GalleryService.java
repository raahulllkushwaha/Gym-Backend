package com.gym.gallery;

import com.gym.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final FileStorageService fileStorageService;

    public GalleryItem upload(MultipartFile file, GalleryItemType type, String caption, String category) {
        String url = fileStorageService.store(file, "gallery");
        GalleryItem item = GalleryItem.builder()
                .url(url)
                .type(type)
                .caption(caption)
                .category(category)
                .visible(true)
                .build();
        return galleryRepository.save(item);
    }

    public List<GalleryItem> publicGallery() {
        return galleryRepository.findByVisibleTrueAndDeletedFalseOrderBySortOrderAsc();
    }

    public List<GalleryItem> allForManager() {
        return galleryRepository.findAllByDeletedFalseOrderBySortOrderAsc();
    }

    public GalleryItem updateCaption(UUID id, String caption, String category) {
        GalleryItem item = getOrThrow(id);
        item.setCaption(caption);
        item.setCategory(category);
        return galleryRepository.save(item);
    }

    public GalleryItem toggleVisibility(UUID id) {
        GalleryItem item = getOrThrow(id);
        item.setVisible(!item.isVisible());
        return galleryRepository.save(item);
    }

    public void delete(UUID id) {
        GalleryItem item = getOrThrow(id);
        fileStorageService.delete(item.getUrl());
        item.setDeleted(true);
        galleryRepository.save(item);
    }

    private GalleryItem getOrThrow(UUID id) {
        return galleryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gallery item not found"));
    }
}
