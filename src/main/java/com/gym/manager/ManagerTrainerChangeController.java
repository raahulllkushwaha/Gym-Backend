package com.gym.manager;

import com.gym.common.ApiResponse;
import com.gym.security.CustomUserDetails;
import com.gym.trainer.TrainerChangeRequest;
import com.gym.trainer.TrainerChangeRequestRepository;
import com.gym.trainer.TrainerChangeRequestStatus;
import com.gym.trainer.TrainerChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manager/trainer-change-requests")
@RequiredArgsConstructor
public class ManagerTrainerChangeController {

    private final TrainerChangeRequestRepository requestRepository;
    private final TrainerChangeService trainerChangeService;

    @GetMapping
    public ApiResponse<Page<TrainerChangeRequest>> listPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Pending trainer change requests",
                requestRepository.findByStatus(TrainerChangeRequestStatus.PENDING, PageRequest.of(page, size)));
    }

    @PostMapping("/{requestId}/approve")
    public ApiResponse<?> approve(@PathVariable UUID requestId, @AuthenticationPrincipal CustomUserDetails manager) {
        trainerChangeService.approve(requestId, manager.getId());
        return ApiResponse.ok("Trainer changed successfully");
    }

    @PostMapping("/{requestId}/reject")
    public ApiResponse<?> reject(@PathVariable UUID requestId, @AuthenticationPrincipal CustomUserDetails manager) {
        trainerChangeService.reject(requestId, manager.getId());
        return ApiResponse.ok("Trainer change request rejected");
    }
}
