package com.gym.manager.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ApproveSignupRequest(
        @NotNull UUID planId,
        UUID trainerUserId
) {}
