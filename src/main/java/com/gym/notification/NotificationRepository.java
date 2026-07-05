package com.gym.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByReceiverUserIdAndDeletedFalseOrderByCreatedAtDesc(UUID receiverUserId, Pageable pageable);
    long countByReceiverUserIdAndReadFalseAndDeletedFalse(UUID receiverUserId);
}
