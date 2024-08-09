package com.practice.mfa.service;

import com.practice.mfa.dto.SendMessageDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class MfaServiceImpl implements MfaService {

    private final RedisTemplate<String, String> redisTemplate;

    private final MailService mailService;

    private static final int SECRET_CODE_LENGTH = 6;

    public MfaServiceImpl(RedisTemplate<String, String> redisTemplate, MailService mailService) {
        this.redisTemplate = redisTemplate;
        this.mailService = mailService;
    }

    public void sendMfaCode(String email) {
        String mfaCode = generateMfaCode(SECRET_CODE_LENGTH);
        redisTemplate.opsForValue().set(email, mfaCode, Duration.ofMinutes(5));

        SendMessageDto sendMessageDto = new SendMessageDto(
                email,
                "Your MFA Code",
                "Your MFA code is " + mfaCode
        );

        mailService.sendMessage(sendMessageDto);
    }

    public boolean verifyMfaCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(email);
        return storedCode != null && storedCode.equals(code);
    }

    public String generateMfaCode(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < length; i++) {
            code.append(secureRandom.nextInt(10));
        }

        return code.toString();
    }
}
