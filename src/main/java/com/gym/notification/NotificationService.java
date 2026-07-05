package com.gym.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification create(UUID receiverUserId, String title, String description) {
        return notificationRepository.save(Notification.builder()
                .receiverUserId(receiverUserId)
                .title(title)
                .description(description)
                .read(false)
                .build());
    }

    /** Used for gym-wide announcements/offers - one row per recipient so read-state is per-user. */
    public void broadcast(List<UUID> receiverUserIds, String title, String description) {
        receiverUserIds.forEach(id -> create(id, title, description));
    }

    public Page<Notification> forUser(UUID userId, Pageable pageable) {
        return notificationRepository.findByReceiverUserIdAndDeletedFalseOrderByCreatedAtDesc(userId, pageable);
    }

    public long unreadCount(UUID userId) {
        return notificationRepository.countByReceiverUserIdAndReadFalseAndDeletedFalse(userId);
    }

    @Transactional
    public void markRead(UUID notificationId, UUID userId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!n.getReceiverUserId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("Not your notification");
        }
        n.setRead(true);
        notificationRepository.save(n);
    }
}
