package com.gym.manager.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectSignupRequest(
        @NotBlank String reason
) {}
