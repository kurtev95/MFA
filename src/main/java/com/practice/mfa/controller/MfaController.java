package com.practice.mfa.controller;

import com.practice.mfa.dto.SendMfaRequest;
import com.practice.mfa.dto.VerifyMfaRequest;
import com.practice.mfa.service.MfaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mfa")
public class MfaController {

    private final MfaService mfaService;

    public MfaController(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @PostMapping("/send")
    public void sendMfaCode(@RequestBody SendMfaRequest request) {
        mfaService.sendMfaCode(request.email());
    }

    @PostMapping("/verify")
    public boolean verifyMfaCode(@RequestBody VerifyMfaRequest request) {
        return mfaService.verifyMfaCode(request.email(), request.code());
    }
}
