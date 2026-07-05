package com.gym.progress.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record AddProgressRequest(
        UUID memberUserId, // required only when a trainer is submitting; ignored/overridden for member self-log
        @NotNull @Positive Double weightKg,
        Double heightCm,
        Double chestCm,
        Double waistCm,
        Double armsCm,
        Double bodyFatPercent,
        String photoUrl
) {}
