# Hlokomela AI

> **Turning water infrastructure data into faster action—before leaks become major failures.**

Hlokomela AI is an AI-powered predictive water monitoring and community response platform designed for South African municipal water networks. It combines **simulated IoT telemetry from ESP32 flow sensors** with **crowdsourced community reports** through a lightweight Community App to identify abnormal water patterns, assess potential infrastructure failures, and prioritise incidents for action.

## Secure AI provider setup

The live assistant uses the configured OpenAI-compatible provider through the Java backend. The API key is never sent to the browser and must be supplied through the deployment secret store or process environment:

```powershell
$env:AI_PROVIDER_ENABLED = "true"
$env:AI_PROVIDER_API_KEY = "set-this-directly-in-your-terminal"
$env:AI_PROVIDER_BASE_URL = "https://api-ap-southeast-1.modelarts-maas.com/openai/v1"
$env:AI_PROVIDER_MODEL = "glm-5.1"
Push-Location backend
mvn spring-boot:run
Pop-Location
```

Do not commit the key, put it in HTML/JavaScript, or paste it into issue trackers or screenshots. The assistant falls back to a safe local response when the provider is disabled or unavailable.

Unlike solutions that monitor infrastructure or collect community reports in isolation, Hlokomela AI brings **IoT sensing, AI-driven risk analysis, and community intelligence** into one connected ecosystem. This enables municipalities to move from **reactive maintenance to proactive, data-driven resource management**, helping reduce water loss, improve response times, and support more sustainable communities.

### 🌍 Our Vision

**To help communities and municipalities care for critical water infrastructure before small warnings become major failures.**

*Hlokomela means "to care for."*
