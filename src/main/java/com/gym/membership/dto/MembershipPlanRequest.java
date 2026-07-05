package com.gym.membership.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MembershipPlanRequest(
        @NotBlank String name,
        String description,
        @Positive int durationInDays,
        @Positive BigDecimal price,
        boolean longTermPlan
) {}
