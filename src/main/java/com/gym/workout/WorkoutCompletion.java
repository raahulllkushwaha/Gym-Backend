package com.gym.workout;

import com.gym.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "workout_completions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"workout_plan_id", "completed_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WorkoutCompletion extends BaseEntity {

    @Column(name = "workout_plan_id", nullable = false)
    private UUID workoutPlanId;

    @Column(name = "completed_date", nullable = false)
    private LocalDate completedDate;
}
