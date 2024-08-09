package com.practice.mfa.service;

public interface MfaService {

    void sendMfaCode(String email) throws RateLimitService.TooManyRequestsException;

    boolean verifyMfaCode(String email, String code) throws RateLimitService.TooManyRequestsException;
}
