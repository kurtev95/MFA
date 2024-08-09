package com.practice.mfa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendMfaRequest(
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email should be valid")
        String email
) {
}
