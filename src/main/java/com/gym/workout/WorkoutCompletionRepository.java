package com.gym.workout;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutCompletionRepository extends JpaRepository<WorkoutCompletion, UUID> {
    Optional<WorkoutCompletion> findByWorkoutPlanIdAndCompletedDate(UUID workoutPlanId, LocalDate date);
    List<WorkoutCompletion> findByWorkoutPlanId(UUID workoutPlanId);
}
