package com.gym.manager;

import com.gym.auth.SignupRequest;
import com.gym.auth.SignupRequestRepository;
import com.gym.auth.SignupRequestStatus;
import com.gym.common.ApiResponse;
import com.gym.manager.dto.ApproveSignupRequest;
import com.gym.manager.dto.CreateMemberRequest;
import com.gym.manager.dto.CreateTrainerRequest;
import com.gym.manager.dto.RejectSignupRequest;
import com.gym.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerUserService managerUserService;
    private final SignupRequestRepository signupRequestRepository;

    // ---- Trainers ----

    @PostMapping("/trainers")
    public ApiResponse<?> createTrainer(@Valid @RequestBody CreateTrainerRequest req,
                                         @AuthenticationPrincipal CustomUserDetails manager) {
        var trainer = managerUserService.createTrainer(req, manager.getId());
        return ApiResponse.ok("Trainer created. Login credentials sent via email & SMS.", trainer.getUsername());
    }

    @DeleteMapping("/trainers/{trainerId}")
    public ApiResponse<?> deleteTrainer(@PathVariable UUID trainerId) {
        managerUserService.deleteTrainer(trainerId);
        return ApiResponse.ok("Trainer removed");
    }

    @PatchMapping("/trainers/{trainerId}/suspend")
    public ApiResponse<?> suspendTrainer(@PathVariable UUID trainerId) {
        managerUserService.suspendTrainer(trainerId);
        return ApiResponse.ok("Trainer suspended");
    }

    @GetMapping("/trainers")
    public ApiResponse<?> listTrainers() {
        return ApiResponse.ok("Trainers", managerUserService.listTrainers());
    }

    // ---- Members ----

    @PostMapping("/members")
    public ApiResponse<?> createMember(@Valid @RequestBody CreateMemberRequest req,
                                        @AuthenticationPrincipal CustomUserDetails manager) {
        var member = managerUserService.createMember(req, manager.getId());
        return ApiResponse.ok("Member created. Login credentials sent via email & SMS.", member.getUsername());
    }

    @DeleteMapping("/members/{memberId}")
    public ApiResponse<?> deleteMember(@PathVariable UUID memberId) {
        managerUserService.deleteMember(memberId);
        return ApiResponse.ok("Member removed");
    }

    // ---- Signup requests (public signup -> pending -> approve/reject) ----

    @GetMapping("/signup-requests")
    public ApiResponse<Page<SignupRequest>> listPendingSignupRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SignupRequest> requests = signupRequestRepository.findByStatus(
                SignupRequestStatus.PENDING, PageRequest.of(page, size));
        return ApiResponse.ok("Pending signup requests", requests);
    }

    @PostMapping("/signup-requests/{requestId}/approve")
    public ApiResponse<?> approveSignupRequest(@PathVariable UUID requestId,
                                                @Valid @RequestBody ApproveSignupRequest approval,
                                                @AuthenticationPrincipal CustomUserDetails manager) {
        var member = managerUserService.approveSignupRequest(requestId, approval, manager.getId());
        return ApiResponse.ok("Approved. Credentials sent via email & SMS.", member.getUsername());
    }

    @PostMapping("/signup-requests/{requestId}/reject")
    public ApiResponse<?> rejectSignupRequest(@PathVariable UUID requestId,
                                               @Valid @RequestBody RejectSignupRequest rejection,
                                               @AuthenticationPrincipal CustomUserDetails manager) {
        managerUserService.rejectSignupRequest(requestId, rejection.reason(), manager.getId());
        return ApiResponse.ok("Signup request rejected");
    }
}
