package com.gym.website;

import com.gym.common.ApiResponse;
import com.gym.website.dto.ContactMessageRequest;
import com.gym.website.dto.TestimonialRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class WebsitePublicController {

    private final WebsiteCmsService cmsService;

    @GetMapping("/settings")
    public ApiResponse<?> settings() {
        return ApiResponse.ok("Website settings", cmsService.getSettings());
    }

    @GetMapping("/announcements")
    public ApiResponse<?> announcements() {
        return ApiResponse.ok("Announcements", cmsService.activeAnnouncements());
    }

    @GetMapping("/offers")
    public ApiResponse<?> offers() {
        return ApiResponse.ok("Offers", cmsService.activeOffers());
    }

    @GetMapping("/testimonials")
    public ApiResponse<?> testimonials() {
        return ApiResponse.ok("Testimonials", cmsService.publicTestimonials());
    }

    @PostMapping("/testimonials")
    public ApiResponse<?> submitTestimonial(@Valid @RequestBody TestimonialRequest testimonial) {
        cmsService.submitTestimonial(testimonial);
        return ApiResponse.ok("Thank you! Your testimonial will appear after manager approval.");
    }

    @GetMapping("/blogs")
    public ApiResponse<?> blogs() {
        return ApiResponse.ok("Blogs", cmsService.publishedBlogs());
    }

    @GetMapping("/faqs")
    public ApiResponse<?> faqs() {
        return ApiResponse.ok("FAQs", cmsService.allFaqs());
    }

    @PostMapping("/contact")
    public ApiResponse<?> contact(@Valid @RequestBody ContactMessageRequest message) {
        cmsService.submitContact(message);
        return ApiResponse.ok("Thank you for contacting us. We'll get back to you soon.");
    }
}
