package com.gym.manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record CreateMemberRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "^[6-9]\\d{9}$") String phoneNumber,
        @NotNull UUID planId,
        UUID trainerUserId // optional at signup time, can be assigned later
) {}
