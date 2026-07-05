package com.gym.diet;

import com.gym.diet.dto.DietPlanRequest;
import com.gym.membership.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DietService {

    private final DietPlanRepository dietPlanRepository;
    private final MembershipRepository membershipRepository;

    @Transactional
    public DietPlan create(DietPlanRequest req, UUID trainerUserId) {
        assertAssigned(trainerUserId, req.memberUserId());
        DietPlan plan = DietPlan.builder()
                .memberUserId(req.memberUserId())
                .trainerUserId(trainerUserId)
                .title(req.title())
                .meals(req.meals())
                .notes(req.notes())
                .build();
        return dietPlanRepository.save(plan);
    }

    @Transactional
    public DietPlan update(UUID planId, DietPlanRequest req, UUID trainerUserId) {
        DietPlan plan = getOrThrow(planId);
        if (!plan.getTrainerUserId().equals(trainerUserId)) {
            throw new AccessDeniedException("You can only edit plans you created");
        }
        plan.setTitle(req.title());
        plan.setMeals(req.meals());
        plan.setNotes(req.notes());
        return dietPlanRepository.save(plan);
    }

    @Transactional
    public void delete(UUID planId, UUID trainerUserId) {
        DietPlan plan = getOrThrow(planId);
        if (!plan.getTrainerUserId().equals(trainerUserId)) {
            throw new AccessDeniedException("You can only delete plans you created");
        }
        plan.setDeleted(true);
        dietPlanRepository.save(plan);
    }

    public List<DietPlan> forMember(UUID memberUserId) {
        return dietPlanRepository.findByMemberUserIdAndDeletedFalse(memberUserId);
    }

    public List<DietPlan> forTrainer(UUID trainerUserId) {
        return dietPlanRepository.findByTrainerUserIdAndDeletedFalse(trainerUserId);
    }

    private void assertAssigned(UUID trainerUserId, UUID memberUserId) {
        var membership = membershipRepository.findByMemberUserIdAndDeletedFalse(memberUserId)
                .orElseThrow(() -> new IllegalArgumentException("Member has no active membership"));
        if (membership.getAssignedTrainerUserId() == null || !membership.getAssignedTrainerUserId().equals(trainerUserId)) {
            throw new AccessDeniedException("This member is not assigned to you");
        }
    }

    private DietPlan getOrThrow(UUID id) {
        return dietPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Diet plan not found"));
    }
}
