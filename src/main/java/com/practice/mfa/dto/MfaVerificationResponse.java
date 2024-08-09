package com.practice.mfa.dto;

public record MfaVerificationResponse(boolean verified, String message) {
}