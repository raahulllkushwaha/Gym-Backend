package com.gym.member;

import com.gym.common.ApiResponse;
import com.gym.membership.Membership;
import com.gym.membership.MembershipRepository;
import com.gym.security.CustomUserDetails;
import com.gym.trainer.TrainerChangeService;
import com.gym.trainer.dto.RequestTrainerChangeDto;
import com.gym.user.User;
import com.gym.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/members/me")
@RequiredArgsConstructor
public class MemberSelfController {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final TrainerChangeService trainerChangeService;

    @GetMapping("/dashboard")
    public ApiResponse<?> dashboard(@AuthenticationPrincipal CustomUserDetails principal) {
        Membership membership = membershipRepository.findByMemberUserIdAndDeletedFalse(principal.getId())
                .orElseThrow(() -> new IllegalStateException("No active membership found"));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("planName", membership.getPlan().getName());
        data.put("status", membership.getStatus());
        data.put("startDate", membership.getStartDate());
        data.put("expiryDate", membership.getExpiryDate());
        data.put("daysRemaining", ChronoUnit.DAYS.between(LocalDate.now(), membership.getExpiryDate()));

        if (membership.getAssignedTrainerUserId() != null) {
            User trainer = userRepository.findById(membership.getAssignedTrainerUserId()).orElse(null);
            data.put("trainerName", trainer != null ? trainer.getFullName() : null);
        } else {
            data.put("trainerName", null);
        }

        return ApiResponse.ok("Member dashboard", data);
    }

    @PostMapping("/request-trainer-change")
    public ApiResponse<?> requestTrainerChange(@Valid @RequestBody RequestTrainerChangeDto dto,
                                                @AuthenticationPrincipal CustomUserDetails principal) {
        trainerChangeService.requestChange(principal.getId(), dto);
        return ApiResponse.ok("Trainer change request submitted. Waiting for manager approval.");
    }
}
