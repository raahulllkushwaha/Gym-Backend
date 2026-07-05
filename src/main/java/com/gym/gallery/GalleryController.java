package com.gym.gallery;

import com.gym.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    // Public - website gallery page
    @GetMapping("/api/public/gallery")
    public ApiResponse<?> publicGallery() {
        return ApiResponse.ok("Gallery", galleryService.publicGallery());
    }

    // Manager only
    @GetMapping("/api/manager/gallery")
    public ApiResponse<?> allGallery() {
        return ApiResponse.ok("All gallery items", galleryService.allForManager());
    }

    @PostMapping(value = "/api/manager/gallery", consumes = "multipart/form-data")
    public ApiResponse<?> upload(@RequestParam("file") MultipartFile file,
                                  @RequestParam GalleryItemType type,
                                  @RequestParam(required = false) String caption,
                                  @RequestParam(required = false) String category) {
        return ApiResponse.ok("Uploaded", galleryService.upload(file, type, caption, category));
    }

    @PutMapping("/api/manager/gallery/{id}")
    public ApiResponse<?> updateCaption(@PathVariable UUID id,
                                         @RequestParam(required = false) String caption,
                                         @RequestParam(required = false) String category) {
        return ApiResponse.ok("Updated", galleryService.updateCaption(id, caption, category));
    }

    @PatchMapping("/api/manager/gallery/{id}/toggle-visibility")
    public ApiResponse<?> toggleVisibility(@PathVariable UUID id) {
        return ApiResponse.ok("Visibility toggled", galleryService.toggleVisibility(id));
    }

    @DeleteMapping("/api/manager/gallery/{id}")
    public ApiResponse<?> delete(@PathVariable UUID id) {
        galleryService.delete(id);
        return ApiResponse.ok("Deleted");
    }
}
