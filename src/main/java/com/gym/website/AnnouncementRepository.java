package com.gym.website;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {
    List<Announcement> findByActiveTrueAndDeletedFalseOrderByCreatedAtDesc();
}
