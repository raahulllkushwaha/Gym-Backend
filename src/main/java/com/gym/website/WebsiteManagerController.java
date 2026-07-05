package com.gym.website;

import com.gym.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manager/website")
@RequiredArgsConstructor
public class WebsiteManagerController {

    private final WebsiteCmsService cmsService;

    // ---- Settings ----
    @PutMapping("/settings")
    public ApiResponse<?> updateSettings(@Valid @RequestBody WebsiteSettings settings) {
        return ApiResponse.ok("Settings updated", cmsService.updateSettings(settings));
    }

    // ---- Announcements ----
    @GetMapping("/announcements")
    public ApiResponse<?> allAnnouncements() { return ApiResponse.ok("Announcements", cmsService.allAnnouncements()); }

    @PostMapping("/announcements")
    public ApiResponse<?> createAnnouncement(@Valid @RequestBody Announcement a) { return ApiResponse.ok("Created", cmsService.createAnnouncement(a)); }

    @DeleteMapping("/announcements/{id}")
    public ApiResponse<?> deleteAnnouncement(@PathVariable UUID id) { cmsService.deleteAnnouncement(id); return ApiResponse.ok("Deleted"); }

    // ---- Offers ----
    @GetMapping("/offers")
    public ApiResponse<?> allOffers() { return ApiResponse.ok("Offers", cmsService.allOffers()); }

    @PostMapping("/offers")
    public ApiResponse<?> createOffer(@Valid @RequestBody Offer o) { return ApiResponse.ok("Created", cmsService.createOffer(o)); }

    @DeleteMapping("/offers/{id}")
    public ApiResponse<?> deleteOffer(@PathVariable UUID id) { cmsService.deleteOffer(id); return ApiResponse.ok("Deleted"); }

    // ---- Testimonials ----
    @GetMapping("/testimonials/pending")
    public ApiResponse<?> pendingTestimonials() { return ApiResponse.ok("Pending testimonials", cmsService.pendingTestimonials()); }

    @PostMapping("/testimonials/{id}/approve")
    public ApiResponse<?> approveTestimonial(@PathVariable UUID id) { return ApiResponse.ok("Approved", cmsService.approveTestimonial(id)); }

    @DeleteMapping("/testimonials/{id}")
    public ApiResponse<?> deleteTestimonial(@PathVariable UUID id) { cmsService.deleteTestimonial(id); return ApiResponse.ok("Deleted"); }

    // ---- Blogs ----
    @GetMapping("/blogs")
    public ApiResponse<?> allBlogs() { return ApiResponse.ok("Blogs", cmsService.allBlogs()); }

    @PostMapping("/blogs")
    public ApiResponse<?> createBlog(@Valid @RequestBody Blog b) { return ApiResponse.ok("Created", cmsService.createBlog(b)); }

    @DeleteMapping("/blogs/{id}")
    public ApiResponse<?> deleteBlog(@PathVariable UUID id) { cmsService.deleteBlog(id); return ApiResponse.ok("Deleted"); }

    // ---- FAQs ----
    @GetMapping("/faqs")
    public ApiResponse<?> allFaqs() { return ApiResponse.ok("FAQs", cmsService.allFaqs()); }

    @PostMapping("/faqs")
    public ApiResponse<?> createFaq(@Valid @RequestBody Faq f) { return ApiResponse.ok("Created", cmsService.createFaq(f)); }

    @DeleteMapping("/faqs/{id}")
    public ApiResponse<?> deleteFaq(@PathVariable UUID id) { cmsService.deleteFaq(id); return ApiResponse.ok("Deleted"); }

    // ---- Contact messages ----
    @GetMapping("/contact-messages")
    public ApiResponse<Page<ContactMessage>> contactMessages(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Contact messages", cmsService.allContactMessages(PageRequest.of(page, size)));
    }
}
