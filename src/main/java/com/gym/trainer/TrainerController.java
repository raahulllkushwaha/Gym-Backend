package com.gym.trainer;

import com.gym.common.ApiResponse;
import com.gym.membership.Membership;
import com.gym.membership.MembershipRepository;
import com.gym.security.CustomUserDetails;
import com.gym.user.User;
import com.gym.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainers/me")
@RequiredArgsConstructor
public class TrainerController {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    @GetMapping("/members")
    public ApiResponse<?> assignedMembers(@AuthenticationPrincipal CustomUserDetails trainer) {
        List<Membership> memberships = membershipRepository.findByAssignedTrainerUserIdAndDeletedFalse(trainer.getId());

        List<Map<String, Object>> result = memberships.stream().map(m -> {
            User member = userRepository.findById(m.getMemberUserId()).orElse(null);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("memberUserId", m.getMemberUserId());
            row.put("fullName", member != null ? member.getFullName() : "Unknown");
            row.put("email", member != null ? member.getEmail() : null);
            row.put("phoneNumber", member != null ? member.getPhoneNumber() : null);
            row.put("planName", m.getPlan().getName());
            row.put("membershipStatus", m.getStatus());
            row.put("expiryDate", m.getExpiryDate());
            return row;
        }).toList();

        return ApiResponse.ok("Assigned members", result);
    }
}
