package com.gym.membership;

import com.gym.membership.dto.MembershipPlanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MembershipPlanService {

    private final MembershipPlanRepository planRepository;

    public List<MembershipPlan> getActivePlans() {
        return planRepository.findByActiveTrueAndDeletedFalse();
    }

    public List<MembershipPlan> getAllPlans() {
        return planRepository.findAll();
    }

    public MembershipPlan createPlan(MembershipPlanRequest req) {
        MembershipPlan plan = MembershipPlan.builder()
                .name(req.name())
                .description(req.description())
                .durationInDays(req.durationInDays())
                .price(req.price())
                .longTermPlan(req.longTermPlan())
                .active(true)
                .build();
        return planRepository.save(plan);
    }

    public MembershipPlan updatePlan(UUID planId, MembershipPlanRequest req) {
        MembershipPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        plan.setName(req.name());
        plan.setDescription(req.description());
        plan.setDurationInDays(req.durationInDays());
        plan.setPrice(req.price());
        plan.setLongTermPlan(req.longTermPlan());
        return planRepository.save(plan);
    }

    public void deactivatePlan(UUID planId) {
        MembershipPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        plan.setActive(false);
        planRepository.save(plan);
    }
}
