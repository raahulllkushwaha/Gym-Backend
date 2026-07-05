package com.gym.website.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record TestimonialRequest(
        @NotBlank String memberName,
        @NotBlank String message,
        String photoUrl,
        @Min(1) @Max(5) Integer rating
) {}
