package com.practice.mfa.dto;

public record VerifyMfaRequest(
        String email,
        String code
) {
}
