package com.gym.trainer;

import com.gym.membership.Membership;
import com.gym.membership.MembershipRepository;
import com.gym.notification.NotificationService;
import com.gym.trainer.dto.RequestTrainerChangeDto;
import com.gym.user.Role;
import com.gym.user.User;
import com.gym.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainerChangeService {

    private final TrainerChangeRequestRepository requestRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public TrainerChangeRequest requestChange(UUID memberUserId, RequestTrainerChangeDto dto) {
        if (requestRepository.existsByMemberUserIdAndStatus(memberUserId, TrainerChangeRequestStatus.PENDING)) {
            throw new IllegalStateException("You already have a pending trainer change request");
        }

        User requestedTrainer = userRepository.findById(dto.requestedTrainerUserId())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));
        if (requestedTrainer.getRole() != Role.TRAINER || requestedTrainer.isDeleted()) {
            throw new IllegalArgumentException("Selected user is not an active trainer");
        }

        Membership membership = membershipRepository.findByMemberUserIdAndDeletedFalse(memberUserId)
                .orElseThrow(() -> new IllegalStateException("No active membership found"));

        TrainerChangeRequest request = TrainerChangeRequest.builder()
                .memberUserId(memberUserId)
                .currentTrainerUserId(membership.getAssignedTrainerUserId())
                .requestedTrainerUserId(dto.requestedTrainerUserId())
                .reason(dto.reason())
                .status(TrainerChangeRequestStatus.PENDING)
                .build();

        return requestRepository.save(request);
    }

    @Transactional
    public void approve(UUID requestId, UUID managerId) {
        TrainerChangeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (request.getStatus() != TrainerChangeRequestStatus.PENDING) {
            throw new IllegalStateException("Request already reviewed");
        }

        Membership membership = membershipRepository.findByMemberUserIdAndDeletedFalse(request.getMemberUserId())
                .orElseThrow(() -> new IllegalStateException("No active membership found for member"));

        membership.setAssignedTrainerUserId(request.getRequestedTrainerUserId());
        membershipRepository.save(membership);

        request.setStatus(TrainerChangeRequestStatus.APPROVED);
        request.setReviewedByUserId(managerId);
        requestRepository.save(request);

        notificationService.create(request.getMemberUserId(), "Trainer Changed",
                "Your trainer has been updated as per your request.");
    }

    @Transactional
    public void reject(UUID requestId, UUID managerId) {
        TrainerChangeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (request.getStatus() != TrainerChangeRequestStatus.PENDING) {
            throw new IllegalStateException("Request already reviewed");
        }
        request.setStatus(TrainerChangeRequestStatus.REJECTED);
        request.setReviewedByUserId(managerId);
        requestRepository.save(request);
    }
}
