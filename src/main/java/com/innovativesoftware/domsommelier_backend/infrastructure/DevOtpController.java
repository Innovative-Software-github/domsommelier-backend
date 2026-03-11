package com.innovativesoftware.domsommelier_backend.infrastructure;

import com.innovativesoftware.domsommelier_backend.auth_management.OtpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Profile({"local", "docker"})
@RestController
@RequestMapping("/dev/otp")
@RequiredArgsConstructor
public class DevOtpController {

    private final RedisService redisService;

    private static final String OTP_PREFIX = "auth:otp:";

    @GetMapping("/code")
    public Map<String, Object> getOtpCode(@RequestParam String email) {
        String key = OTP_PREFIX + email;
        OtpSession session = redisService.getObject(key, OtpSession.class);
        Long ttl = redisService.getExpire(key);

        if (session != null && session.getCode() != null) {
            return Map.of(
                    "code", session.getCode(),
                    "ttl", ttl != null && ttl > 0 ? ttl : 0,
                    "found", true
            );
        }
        return Map.of("found", false, "ttl", 0);
    }
}
