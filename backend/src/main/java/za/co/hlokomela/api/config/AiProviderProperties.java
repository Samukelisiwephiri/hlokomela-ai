package za.co.hlokomela.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the server-side AI provider client.
 */
@ConfigurationProperties(prefix = "app.ai")
public class AiProviderProperties {
    private boolean enabled = false;
    private String baseUrl = "https://api-ap-southeast-1.modelarts-maas.com/openai/v1";
    private String apiKey;
    private String model = "glm-5.1";
    private int timeoutSeconds = 20;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}
