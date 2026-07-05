package com.gym.progress;

import com.gym.common.ApiResponse;
import com.gym.progress.dto.AddProgressRequest;
import com.gym.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/api/members/me/progress")
    public ApiResponse<?> addSelf(@Valid @RequestBody AddProgressRequest req, @AuthenticationPrincipal CustomUserDetails member) {
        return ApiResponse.ok("Progress logged", progressService.addSelf(member.getId(), req));
    }

    @GetMapping("/api/members/me/progress")
    public ApiResponse<?> myHistory(@AuthenticationPrincipal CustomUserDetails member) {
        return ApiResponse.ok("Progress history", progressService.history(member.getId()));
    }

    @PostMapping("/api/trainers/progress")
    public ApiResponse<?> addByTrainer(@Valid @RequestBody AddProgressRequest req, @AuthenticationPrincipal CustomUserDetails trainer) {
        return ApiResponse.ok("Progress logged for member", progressService.addByTrainer(trainer.getId(), req));
    }

    // Public-ish utility (behind auth via anyRequest().authenticated() default rule) - no DB write
    @GetMapping("/api/utils/bmi")
    public ApiResponse<?> bmi(@RequestParam double weightKg, @RequestParam double heightCm) {
        return ApiResponse.ok("BMI calculated", progressService.calculateBmi(weightKg, heightCm));
    }
}
