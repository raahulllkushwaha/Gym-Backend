package com.gym.auth.dto;

import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String username,
        String role,
        boolean mustChangePassword
) {}
