package com.practice.mfa.controller;

import com.practice.mfa.dto.MfaVerificationResponse;
import com.practice.mfa.dto.SendMfaRequest;
import com.practice.mfa.dto.VerifyMfaRequest;
import com.practice.mfa.service.MfaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mfa")
public class MfaController {

    private final MfaService mfaService;

    public MfaController(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendMfaCode(@RequestBody SendMfaRequest request) {
        mfaService.sendMfaCode(request.email());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/verify")
    public ResponseEntity<MfaVerificationResponse> verifyMfaCode(@RequestBody VerifyMfaRequest request) {
        boolean isVerified = mfaService.verifyMfaCode(request.email(), request.code());
        MfaVerificationResponse response = new MfaVerificationResponse(isVerified);
        return new ResponseEntity<>(response, isVerified ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
    }
}
