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

    private final RateLimitService rateLimitService;

    private static final int SECRET_CODE_LENGTH = 6;
    private static final String RATE_LIMIT_SEND_PREFIX = "mfa:ratelimit:send:";
    private static final String RATE_LIMIT_VERIFY_PREFIX = "mfa:ratelimit:verify:";
    private static final String MFA_CODE_PREFIX = "mfa:code:";

    public MfaServiceImpl(RedisTemplate<String, String> redisTemplate, MailService mailService, RateLimitService rateLimitService) {
        this.redisTemplate = redisTemplate;
        this.mailService = mailService;
        this.rateLimitService = rateLimitService;
    }

    public void sendMfaCode(String email) throws RateLimitService.TooManyRequestsException {
        rateLimitService.handleRateLimit(email,RATE_LIMIT_SEND_PREFIX);

        String mfaCode = generateMfaCode(SECRET_CODE_LENGTH);
        redisTemplate.opsForValue().set(MFA_CODE_PREFIX + email, mfaCode, Duration.ofMinutes(5));

        SendMessageDto sendMessageDto = new SendMessageDto(
                email,
                "Your MFA Code",
                "Your MFA code is " + mfaCode
        );

        mailService.sendMessage(sendMessageDto);
    }

    public boolean verifyMfaCode(String email, String code) throws RateLimitService.TooManyRequestsException {
        rateLimitService.handleRateLimit(email,RATE_LIMIT_VERIFY_PREFIX);

        String storedCode = redisTemplate.opsForValue().get(MFA_CODE_PREFIX + email);
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
