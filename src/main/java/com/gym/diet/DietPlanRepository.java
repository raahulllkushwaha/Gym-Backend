package com.gym.diet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DietPlanRepository extends JpaRepository<DietPlan, UUID> {
    List<DietPlan> findByMemberUserIdAndDeletedFalse(UUID memberUserId);
    List<DietPlan> findByTrainerUserIdAndDeletedFalse(UUID trainerUserId);
}
