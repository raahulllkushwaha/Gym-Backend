package com.gym.workout;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, UUID> {
    List<WorkoutPlan> findByMemberUserIdAndDeletedFalse(UUID memberUserId);
    List<WorkoutPlan> findByTrainerUserIdAndDeletedFalse(UUID trainerUserId);
}
