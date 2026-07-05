package com.gym.workout;

import com.gym.membership.MembershipRepository;
import com.gym.workout.dto.WorkoutPlanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutCompletionRepository completionRepository;
    private final MembershipRepository membershipRepository;

    @Transactional
    public WorkoutPlan create(WorkoutPlanRequest req, UUID trainerUserId) {
        assertTrainerAssignedToMember(trainerUserId, req.memberUserId());

        WorkoutPlan plan = WorkoutPlan.builder()
                .memberUserId(req.memberUserId())
                .trainerUserId(trainerUserId)
                .dayOfWeek(req.dayOfWeek())
                .title(req.title())
                .exercises(req.exercises())
                .notes(req.notes())
                .build();
        return workoutPlanRepository.save(plan);
    }

    @Transactional
    public WorkoutPlan update(UUID planId, WorkoutPlanRequest req, UUID trainerUserId) {
        WorkoutPlan plan = getOrThrow(planId);
        if (!plan.getTrainerUserId().equals(trainerUserId)) {
            throw new AccessDeniedException("You can only edit plans you created");
        }
        plan.setDayOfWeek(req.dayOfWeek());
        plan.setTitle(req.title());
        plan.setExercises(req.exercises());
        plan.setNotes(req.notes());
        return workoutPlanRepository.save(plan);
    }

    @Transactional
    public void delete(UUID planId, UUID trainerUserId) {
        WorkoutPlan plan = getOrThrow(planId);
        if (!plan.getTrainerUserId().equals(trainerUserId)) {
            throw new AccessDeniedException("You can only delete plans you created");
        }
        plan.setDeleted(true);
        workoutPlanRepository.save(plan);
    }

    public List<WorkoutPlan> forMember(UUID memberUserId) {
        return workoutPlanRepository.findByMemberUserIdAndDeletedFalse(memberUserId);
    }

    public List<WorkoutPlan> forTrainer(UUID trainerUserId) {
        return workoutPlanRepository.findByTrainerUserIdAndDeletedFalse(trainerUserId);
    }

    @Transactional
    public void markCompleted(UUID planId, UUID memberUserId) {
        WorkoutPlan plan = getOrThrow(planId);
        if (!plan.getMemberUserId().equals(memberUserId)) {
            throw new AccessDeniedException("This is not your workout plan");
        }
        LocalDate today = LocalDate.now();
        if (completionRepository.findByWorkoutPlanIdAndCompletedDate(planId, today).isPresent()) {
            throw new IllegalStateException("Already marked completed for today");
        }
        completionRepository.save(WorkoutCompletion.builder()
                .workoutPlanId(planId)
                .completedDate(today)
                .build());
    }

    public List<WorkoutCompletion> completionHistory(UUID planId) {
        return completionRepository.findByWorkoutPlanId(planId);
    }

    private void assertTrainerAssignedToMember(UUID trainerUserId, UUID memberUserId) {
        var membership = membershipRepository.findByMemberUserIdAndDeletedFalse(memberUserId)
                .orElseThrow(() -> new IllegalArgumentException("Member has no active membership"));
        if (membership.getAssignedTrainerUserId() == null || !membership.getAssignedTrainerUserId().equals(trainerUserId)) {
            throw new AccessDeniedException("This member is not assigned to you");
        }
    }

    private WorkoutPlan getOrThrow(UUID id) {
        return workoutPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workout plan not found"));
    }
}
