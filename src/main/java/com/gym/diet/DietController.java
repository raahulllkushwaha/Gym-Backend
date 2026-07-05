package com.gym.diet;

import com.gym.common.ApiResponse;
import com.gym.diet.dto.DietPlanRequest;
import com.gym.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DietController {

    private final DietService dietService;

    @PostMapping("/api/trainers/diet-plans")
    public ApiResponse<?> create(@Valid @RequestBody DietPlanRequest req, @AuthenticationPrincipal CustomUserDetails trainer) {
        return ApiResponse.ok("Diet plan created", dietService.create(req, trainer.getId()));
    }

    @PutMapping("/api/trainers/diet-plans/{planId}")
    public ApiResponse<?> update(@PathVariable UUID planId, @Valid @RequestBody DietPlanRequest req,
                                  @AuthenticationPrincipal CustomUserDetails trainer) {
        return ApiResponse.ok("Diet plan updated", dietService.update(planId, req, trainer.getId()));
    }

    @DeleteMapping("/api/trainers/diet-plans/{planId}")
    public ApiResponse<?> delete(@PathVariable UUID planId, @AuthenticationPrincipal CustomUserDetails trainer) {
        dietService.delete(planId, trainer.getId());
        return ApiResponse.ok("Diet plan deleted");
    }

    @GetMapping("/api/trainers/me/diet-plans")
    public ApiResponse<?> myCreatedPlans(@AuthenticationPrincipal CustomUserDetails trainer) {
        return ApiResponse.ok("Diet plans you created", dietService.forTrainer(trainer.getId()));
    }

    @GetMapping("/api/members/me/diet-plans")
    public ApiResponse<?> myDietPlans(@AuthenticationPrincipal CustomUserDetails member) {
        return ApiResponse.ok("Your diet plans", dietService.forMember(member.getId()));
    }
}
