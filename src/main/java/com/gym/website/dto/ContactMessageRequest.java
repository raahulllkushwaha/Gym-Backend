package com.gym.website.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContactMessageRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        String phoneNumber,
        @NotBlank String message
) {}
