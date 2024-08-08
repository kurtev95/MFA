package com.practice.mfa.service;

import com.practice.mfa.dto.SendMessageDto;

public interface MailService {
    void sendMessage(SendMessageDto sendMessageDto);
}
