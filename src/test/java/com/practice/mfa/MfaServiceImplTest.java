package com.practice.mfa;

import com.practice.mfa.dto.SendMessageDto;
import com.practice.mfa.service.MailService;
import com.practice.mfa.service.MfaServiceImpl;
import com.practice.mfa.service.RateLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.mockito.Mockito.*;

public class MfaServiceImplTest {

    public static final String PREFIX = "mfa:code:";

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private MailService mailService;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private RateLimitService rateLimitService;

    @InjectMocks
    private MfaServiceImpl mfaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testSendMfaCode() throws RateLimitService.TooManyRequestsException {
        String email = "test@example.com";
        String generatedCode = "123456";

        // Mocking the behavior of generateMfaCode to return a fixed code for predictability in tests
        MfaServiceImpl spyMfaService = spy(mfaService);
        doReturn(generatedCode).when(spyMfaService).generateMfaCode(anyInt());
        doNothing().when(rateLimitService).handleRateLimit(email,"mfa:ratelimit:send:");

        // Perform the operation
        spyMfaService.sendMfaCode(email);

        // Verify that the RedisTemplate's opsForValue().set() method was called with the correct arguments
        verify(valueOperations, Mockito.times(1)).set(eq(PREFIX+email), eq(generatedCode), any(Duration.class));

        // Verify that the MailService's sendMessage() method was called with the correct arguments
        SendMessageDto expectedDto = new SendMessageDto(email, "Your MFA Code", "Your MFA code is " + generatedCode);
        verify(mailService).sendMessage(eq(expectedDto));
    }

    @Test
    void testVerifyMfaCode_ValidCode() throws RateLimitService.TooManyRequestsException {
        String email = "test@example.com";
        String code = "123456";

        doNothing().when(rateLimitService).handleRateLimit(email,"mfa:ratelimit:verify:");
        // Mock Redis to return the code
        when(valueOperations.get(PREFIX+email)).thenReturn(code);

        boolean result = mfaService.verifyMfaCode(email, code);

        // Verify that the result is true
        assert (result);
    }

    @Test
    void testVerifyMfaCode_InvalidCode() throws RateLimitService.TooManyRequestsException {
        String email = "test@example.com";
        String code = "123456";

        doNothing().when(rateLimitService).handleRateLimit(email,"mfa:ratelimit:verify:");
        // Mock Redis to return a different code
        when(redisTemplate.opsForValue().get(email)).thenReturn("654321");

        boolean result = mfaService.verifyMfaCode(email, code);

        // Verify that the result is false
        assert (!result);
    }

    @Test
    void testVerifyMfaCode_NullCode() throws RateLimitService.TooManyRequestsException {
        String email = "test@example.com";

        doNothing().when(rateLimitService).handleRateLimit(email,"mfa:ratelimit:verify:");
        // Mock Redis to return null
        when(redisTemplate.opsForValue().get(email)).thenReturn(null);

        boolean result = mfaService.verifyMfaCode(email, "anycode");

        // Verify that the result is false
        assert (!result);
    }
}

