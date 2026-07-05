package com.gym.website;

import com.gym.notification.NotificationService;
import com.gym.user.Role;
import com.gym.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebsiteCmsService {

    private final WebsiteSettingsRepository settingsRepository;
    private final AnnouncementRepository announcementRepository;
    private final OfferRepository offerRepository;
    private final TestimonialRepository testimonialRepository;
    private final BlogRepository blogRepository;
    private final FaqRepository faqRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // ---- Settings (singleton) ----
    public WebsiteSettings getSettings() {
        return settingsRepository.findAll().stream().findFirst()
                .orElseGet(() -> settingsRepository.save(
                        WebsiteSettings.builder().gymName("My Gym").build()));
    }

    @Transactional
    public WebsiteSettings updateSettings(WebsiteSettings updated) {
        WebsiteSettings settings = getSettings();
        updated.setId(settings.getId());
        settings.setGymName(updated.getGymName());
        settings.setPhoneNumber(updated.getPhoneNumber());
        settings.setEmail(updated.getEmail());
        settings.setOpeningHours(updated.getOpeningHours());
        settings.setLogoUrl(updated.getLogoUrl());
        settings.setHeroBannerUrl(updated.getHeroBannerUrl());
        settings.setHeroTagline(updated.getHeroTagline());
        settings.setAboutUs(updated.getAboutUs());
        settings.setFooterText(updated.getFooterText());
        settings.setAddress(updated.getAddress());
        settings.setInstagramUrl(updated.getInstagramUrl());
        settings.setFacebookUrl(updated.getFacebookUrl());
        settings.setYoutubeUrl(updated.getYoutubeUrl());
        settings.setWhatsappNumber(updated.getWhatsappNumber());
        return settingsRepository.save(settings);
    }

    // ---- Announcements ----
    public List<Announcement> activeAnnouncements() { return announcementRepository.findByActiveTrueAndDeletedFalseOrderByCreatedAtDesc(); }
    public List<Announcement> allAnnouncements() { return announcementRepository.findAll(); }

    @Transactional
    public Announcement createAnnouncement(Announcement a) {
        Announcement saved = announcementRepository.save(a);
        List<UUID> memberIds = userRepository.findAll().stream()
                .filter(u -> !u.isDeleted() && u.getRole() == Role.MEMBER)
                .map(u -> u.getId())
                .toList();
        notificationService.broadcast(memberIds, saved.getTitle(), saved.getMessage());
        return saved;
    }

    public void deleteAnnouncement(UUID id) { toggleDelete(announcementRepository, id); }

    // ---- Offers ----
    public List<Offer> activeOffers() { return offerRepository.findByActiveTrueAndDeletedFalseOrderByCreatedAtDesc(); }
    public List<Offer> allOffers() { return offerRepository.findAll(); }
    public Offer createOffer(Offer o) { return offerRepository.save(o); }
    public void deleteOffer(UUID id) { toggleDelete(offerRepository, id); }

    // ---- Testimonials ----
    public List<Testimonial> publicTestimonials() { return testimonialRepository.findByApprovedTrueAndHiddenFalseAndDeletedFalseOrderByCreatedAtDesc(); }
    public List<Testimonial> pendingTestimonials() { return testimonialRepository.findByApprovedFalseAndDeletedFalse(); }

    public Testimonial submitTestimonial(com.gym.website.dto.TestimonialRequest req) {
        Testimonial t = Testimonial.builder()
                .memberName(req.memberName())
                .message(req.message())
                .photoUrl(req.photoUrl())
                .rating(req.rating())
                .approved(false)
                .hidden(false)
                .build();
        return testimonialRepository.save(t);
    }

    @Transactional
    public Testimonial approveTestimonial(UUID id) {
        Testimonial t = testimonialRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Testimonial not found"));
        t.setApproved(true);
        return testimonialRepository.save(t);
    }

    public void deleteTestimonial(UUID id) { toggleDelete(testimonialRepository, id); }

    // ---- Blogs ----
    public List<Blog> publishedBlogs() { return blogRepository.findByPublishedTrueAndDeletedFalseOrderByCreatedAtDesc(); }
    public List<Blog> allBlogs() { return blogRepository.findAll(); }
    public Blog createBlog(Blog b) { return blogRepository.save(b); }
    public void deleteBlog(UUID id) { toggleDelete(blogRepository, id); }

    // ---- FAQs ----
    public List<Faq> allFaqs() { return faqRepository.findByDeletedFalseOrderBySortOrderAsc(); }
    public Faq createFaq(Faq f) { return faqRepository.save(f); }
    public void deleteFaq(UUID id) { toggleDelete(faqRepository, id); }

    // ---- Contact form ----
    public ContactMessage submitContact(com.gym.website.dto.ContactMessageRequest req) {
        ContactMessage message = ContactMessage.builder()
                .name(req.name())
                .email(req.email())
                .phoneNumber(req.phoneNumber())
                .message(req.message())
                .read(false)
                .build();
        return contactMessageRepository.save(message);
    }
    public org.springframework.data.domain.Page<ContactMessage> allContactMessages(org.springframework.data.domain.Pageable pageable) {
        return contactMessageRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    // ---- helper ----
    private <T extends com.gym.common.BaseEntity> void toggleDelete(org.springframework.data.jpa.repository.JpaRepository<T, UUID> repo, UUID id) {
        T entity = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        entity.setDeleted(true);
        repo.save(entity);
    }
}
