package za.co.hlokomela.api.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.stereotype.Service;
import za.co.hlokomela.api.config.AppSecurityProperties;
import za.co.hlokomela.api.exception.DeviceAuthenticationException;

@Service
public class DeviceKeyValidator {
    private final AppSecurityProperties securityProperties;

    public DeviceKeyValidator(AppSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public void requireValid(String presentedKey) {
        String expectedKey = securityProperties.getDeviceApiKey();
        if (presentedKey == null || expectedKey == null
            || !MessageDigest.isEqual(expectedKey.getBytes(StandardCharsets.UTF_8),
                presentedKey.getBytes(StandardCharsets.UTF_8))) {
            throw new DeviceAuthenticationException("Invalid device API key");
        }
    }
}
