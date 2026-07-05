package com.gym.membership;

import com.gym.common.ApiResponse;
import com.gym.membership.dto.MembershipPlanRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService planService;

    // Public - shown on website pricing page
    @GetMapping("/api/public/plans")
    public ApiResponse<?> getActivePlans() {
        return ApiResponse.ok("Active plans", planService.getActivePlans());
    }

    // Manager only
    @GetMapping("/api/manager/plans")
    public ApiResponse<?> getAllPlans() {
        return ApiResponse.ok("All plans", planService.getAllPlans());
    }

    @PostMapping("/api/manager/plans")
    public ApiResponse<?> createPlan(@Valid @RequestBody MembershipPlanRequest req) {
        return ApiResponse.ok("Plan created", planService.createPlan(req));
    }

    @PutMapping("/api/manager/plans/{planId}")
    public ApiResponse<?> updatePlan(@PathVariable UUID planId, @Valid @RequestBody MembershipPlanRequest req) {
        return ApiResponse.ok("Plan updated", planService.updatePlan(planId, req));
    }

    @DeleteMapping("/api/manager/plans/{planId}")
    public ApiResponse<?> deactivatePlan(@PathVariable UUID planId) {
        planService.deactivatePlan(planId);
        return ApiResponse.ok("Plan deactivated");
    }
}
