package com.gym.workout.dto;

import com.gym.workout.WeekDay;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record WorkoutPlanRequest(
        @NotNull UUID memberUserId,
        @NotNull WeekDay dayOfWeek,
        @NotBlank String title,
        @NotEmpty List<String> exercises,
        String notes
) {}
