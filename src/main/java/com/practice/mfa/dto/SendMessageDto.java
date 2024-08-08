package com.practice.mfa.dto;

public record SendMessageDto(
        String email,
        String subject,
        String text
) {
}
