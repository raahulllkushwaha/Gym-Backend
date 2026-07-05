package com.gym.trainer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrainerChangeRequestRepository extends JpaRepository<TrainerChangeRequest, UUID> {
    Page<TrainerChangeRequest> findByStatus(TrainerChangeRequestStatus status, Pageable pageable);
    boolean existsByMemberUserIdAndStatus(UUID memberUserId, TrainerChangeRequestStatus status);
}
