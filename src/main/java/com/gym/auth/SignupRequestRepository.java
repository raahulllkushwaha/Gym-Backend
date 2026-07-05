package com.gym.auth;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SignupRequestRepository extends JpaRepository<SignupRequest, UUID> {
    Page<SignupRequest> findByStatus(SignupRequestStatus status, Pageable pageable);
    boolean existsByEmailAndStatus(String email, SignupRequestStatus status);
}
