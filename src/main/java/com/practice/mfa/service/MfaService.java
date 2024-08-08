package com.practice.mfa.service;

public interface MfaService {

    void sendMfaCode(String email);

    boolean verifyMfaCode(String email, String code);
}
