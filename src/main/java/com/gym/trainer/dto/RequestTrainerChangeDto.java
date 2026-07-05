package com.gym.trainer.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RequestTrainerChangeDto(
        @NotNull UUID requestedTrainerUserId,
        String reason
) {}
