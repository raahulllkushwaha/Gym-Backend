package com.gym.workout;

import com.gym.common.ApiResponse;
import com.gym.security.CustomUserDetails;
import com.gym.workout.dto.WorkoutPlanRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    // ---- Trainer ----
    @PostMapping("/api/trainers/workout-plans")
    public ApiResponse<?> create(@Valid @RequestBody WorkoutPlanRequest req,
                                  @AuthenticationPrincipal CustomUserDetails trainer) {
        return ApiResponse.ok("Workout plan created", workoutService.create(req, trainer.getId()));
    }

    @PutMapping("/api/trainers/workout-plans/{planId}")
    public ApiResponse<?> update(@PathVariable UUID planId, @Valid @RequestBody WorkoutPlanRequest req,
                                  @AuthenticationPrincipal CustomUserDetails trainer) {
        return ApiResponse.ok("Workout plan updated", workoutService.update(planId, req, trainer.getId()));
    }

    @DeleteMapping("/api/trainers/workout-plans/{planId}")
    public ApiResponse<?> delete(@PathVariable UUID planId, @AuthenticationPrincipal CustomUserDetails trainer) {
        workoutService.delete(planId, trainer.getId());
        return ApiResponse.ok("Workout plan deleted");
    }

    @GetMapping("/api/trainers/me/workout-plans")
    public ApiResponse<?> myCreatedPlans(@AuthenticationPrincipal CustomUserDetails trainer) {
        return ApiResponse.ok("Workout plans you created", workoutService.forTrainer(trainer.getId()));
    }

    // ---- Member ----
    @GetMapping("/api/members/me/workout-plans")
    public ApiResponse<?> myWorkoutPlans(@AuthenticationPrincipal CustomUserDetails member) {
        return ApiResponse.ok("Your workout plans", workoutService.forMember(member.getId()));
    }

    @PostMapping("/api/members/me/workout-plans/{planId}/complete")
    public ApiResponse<?> markCompleted(@PathVariable UUID planId, @AuthenticationPrincipal CustomUserDetails member) {
        workoutService.markCompleted(planId, member.getId());
        return ApiResponse.ok("Marked as completed for today");
    }
}
