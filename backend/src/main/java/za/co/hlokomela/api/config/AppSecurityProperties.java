package za.co.hlokomela.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {
    private String jwtSecret;
    private long jwtExpirationMinutes = 120;
    private String deviceApiKey;

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public long getJwtExpirationMinutes() {
        return jwtExpirationMinutes;
    }

    public void setJwtExpirationMinutes(long jwtExpirationMinutes) {
        this.jwtExpirationMinutes = jwtExpirationMinutes;
    }

    public String getDeviceApiKey() {
        return deviceApiKey;
    }

    public void setDeviceApiKey(String deviceApiKey) {
        this.deviceApiKey = deviceApiKey;
    }
}
