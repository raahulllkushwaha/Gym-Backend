package com.gym.workout;

import com.gym.common.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workout_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WorkoutPlan extends BaseEntity {

    @Column(nullable = false)
    private UUID memberUserId;

    @Column(nullable = false)
    private UUID trainerUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private WeekDay dayOfWeek;

    @Column(nullable = false)
    private String title; // e.g. "Chest & Triceps"

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "workout_plan_exercises", joinColumns = @JoinColumn(name = "workout_plan_id"))
    @Column(name = "exercise_line", length = 255)
    @lombok.Builder.Default
    private List<String> exercises = new ArrayList<>(); // e.g. "Bench Press - 4x10", "Incline DB Press - 3x12"

    @Column(length = 500)
    private String notes;
}
