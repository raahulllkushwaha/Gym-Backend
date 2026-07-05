package com.gym.manager;

import com.gym.auth.SignupRequest;
import com.gym.auth.SignupRequestRepository;
import com.gym.auth.SignupRequestStatus;
import com.gym.manager.dto.ApproveSignupRequest;
import com.gym.manager.dto.CreateMemberRequest;
import com.gym.manager.dto.CreateTrainerRequest;
import com.gym.membership.Membership;
import com.gym.membership.MembershipPlan;
import com.gym.membership.MembershipPlanRepository;
import com.gym.membership.MembershipRepository;
import com.gym.membership.MembershipStatus;
import com.gym.user.Role;
import com.gym.user.User;
import com.gym.user.UserProvisioningService;
import com.gym.user.UserRepository;
import com.gym.user.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManagerUserService {

    private final UserRepository userRepository;
    private final UserProvisioningService provisioningService;
    private final MembershipPlanRepository planRepository;
    private final MembershipRepository membershipRepository;
    private final SignupRequestRepository signupRequestRepository;

    // ---------------- Trainer ----------------

    @Transactional
    public User createTrainer(CreateTrainerRequest req, UUID managerId) {
        return provisioningService.provisionUser(req.fullName(), req.email(), req.phoneNumber(), Role.TRAINER, managerId);
    }

    @Transactional
    public void deleteTrainer(UUID trainerId) {
        User trainer = getUserByIdAndRole(trainerId, Role.TRAINER);
        trainer.setDeleted(true);
        userRepository.save(trainer);
        // unassign trainer from any active memberships
        membershipRepository.findByAssignedTrainerUserIdAndDeletedFalse(trainerId)
                .forEach(m -> { m.setAssignedTrainerUserId(null); membershipRepository.save(m); });
    }

    @Transactional
    public void suspendTrainer(UUID trainerId) {
        User trainer = getUserByIdAndRole(trainerId, Role.TRAINER);
        trainer.setStatus(UserStatus.SUSPENDED);
        userRepository.save(trainer);
    }

    public List<User> listTrainers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.TRAINER && !u.isDeleted())
                .toList();
    }

    // ---------------- Member ----------------

    @Transactional
    public User createMember(CreateMemberRequest req, UUID managerId) {
        MembershipPlan plan = planRepository.findById(req.planId())
                .orElseThrow(() -> new IllegalArgumentException("Membership plan not found"));

        if (req.trainerUserId() != null) {
            getUserByIdAndRole(req.trainerUserId(), Role.TRAINER); // validates existence
        }

        User member = provisioningService.provisionUser(req.fullName(), req.email(), req.phoneNumber(), Role.MEMBER, managerId);

        Membership membership = Membership.builder()
                .memberUserId(member.getId())
                .plan(plan)
                .assignedTrainerUserId(req.trainerUserId())
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(plan.getDurationInDays()))
                .status(MembershipStatus.ACTIVE)
                .build();
        membershipRepository.save(membership);

        return member;
    }

    @Transactional
    public void deleteMember(UUID memberId) {
        User member = getUserByIdAndRole(memberId, Role.MEMBER);
        member.setDeleted(true);
        userRepository.save(member);
        membershipRepository.findByMemberUserIdAndDeletedFalse(memberId)
                .ifPresent(m -> { m.setDeleted(true); m.setStatus(MembershipStatus.CANCELLED); membershipRepository.save(m); });
    }

    // ---------------- Signup request approval ----------------

    @Transactional
    public User approveSignupRequest(UUID requestId, ApproveSignupRequest approval, UUID managerId) {
        SignupRequest signupRequest = signupRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Signup request not found"));

        if (signupRequest.getStatus() != SignupRequestStatus.PENDING) {
            throw new IllegalStateException("This request has already been reviewed");
        }

        MembershipPlan plan = planRepository.findById(approval.planId())
                .orElseThrow(() -> new IllegalArgumentException("Membership plan not found"));

        User member = provisioningService.provisionUser(
                signupRequest.getFullName(), signupRequest.getEmail(), signupRequest.getPhoneNumber(),
                Role.MEMBER, managerId);

        Membership membership = Membership.builder()
                .memberUserId(member.getId())
                .plan(plan)
                .assignedTrainerUserId(approval.trainerUserId())
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(plan.getDurationInDays()))
                .status(MembershipStatus.ACTIVE)
                .build();
        membershipRepository.save(membership);

        signupRequest.setStatus(SignupRequestStatus.APPROVED);
        signupRequest.setReviewedByUserId(managerId);
        signupRequestRepository.save(signupRequest);

        return member;
    }

    @Transactional
    public void rejectSignupRequest(UUID requestId, String reason, UUID managerId) {
        SignupRequest signupRequest = signupRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Signup request not found"));

        if (signupRequest.getStatus() != SignupRequestStatus.PENDING) {
            throw new IllegalStateException("This request has already been reviewed");
        }

        signupRequest.setStatus(SignupRequestStatus.REJECTED);
        signupRequest.setRejectionReason(reason);
        signupRequest.setReviewedByUserId(managerId);
        signupRequestRepository.save(signupRequest);
    }

    // ---------------- helpers ----------------

    private User getUserByIdAndRole(UUID userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getRole() != role || user.isDeleted()) {
            throw new IllegalArgumentException("User is not a " + role.name());
        }
        return user;
    }
}
