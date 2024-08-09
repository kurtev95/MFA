package com.practice.mfa.controller;

import com.practice.mfa.dto.MfaVerificationResponse;
import com.practice.mfa.dto.SendMfaRequest;
import com.practice.mfa.dto.VerifyMfaRequest;
import com.practice.mfa.service.MfaService;
import com.practice.mfa.service.RateLimitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mfa")
public class MfaController {

    private final MfaService mfaService;

    public MfaController(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMfaCode(@Valid @RequestBody SendMfaRequest request) {
        try {
            mfaService.sendMfaCode(request.email());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RateLimitService.TooManyRequestsException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<MfaVerificationResponse> verifyMfaCode(@Valid @RequestBody VerifyMfaRequest request) {
        try {
            var isVerified = mfaService.verifyMfaCode(request.email(), request.code());
            MfaVerificationResponse response = new MfaVerificationResponse(isVerified, isVerified ? "MFA code verified successfully." : "Invalid MFA code.");
            return new ResponseEntity<>(response, isVerified ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
        } catch (RateLimitService.TooManyRequestsException e) {
            RateLimitService.TooManyRequestsException rateLimitResponse = new RateLimitService.TooManyRequestsException(e.getMessage());
            MfaVerificationResponse response = new MfaVerificationResponse(false, rateLimitResponse.getMessage());
            return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
        }

    }
}
