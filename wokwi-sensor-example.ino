#include <WiFi.h>
#include <PubSubClient.h>

const char* ssid = "Wokwi-GUEST";
const char* password = "";

// These match your three Wokwi potentiometer connections.
const int FLOW_PIN = 34;
const int PRESSURE_PIN = 35;
const int VIBRATION_PIN = 32;

// Use the matching WebSocket URL and topic in iot-dashboard.html.
// The ESP32 connects using normal MQTT TCP; the website uses MQTT over WebSockets.
const char* MQTT_HOST = "broker.emqx.io";
const int MQTT_PORT = 1883;
const char* MQTT_TOPIC = "hlokomela/demo/pipe-01";

WiFiClient wifiClient;
PubSubClient mqtt(wifiClient);

void connectWiFi() {
  WiFi.begin(ssid, password);
  Serial.print("Connecting to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();
  Serial.println("WiFi Connected!");
}

void connectMQTT() {
  while (!mqtt.connected()) {
    String clientId = "hlokomela-pipe-01-" + String((uint32_t)ESP.getEfuseMac(), HEX);
    Serial.print("Connecting to MQTT...");
    if (mqtt.connect(clientId.c_str())) {
      Serial.println(" connected!");
    } else {
      Serial.println(" retrying");
      delay(1500);
    }
  }
}

void setup() {
  Serial.begin(115200);
  connectWiFi();
  mqtt.setServer(MQTT_HOST, MQTT_PORT);
}

void loop() {
  if (WiFi.status() != WL_CONNECTED) connectWiFi();
  if (!mqtt.connected()) connectMQTT();
  mqtt.loop();

  int flowRaw = analogRead(FLOW_PIN);
  int pressureRaw = analogRead(PRESSURE_PIN);
  int vibrationRaw = analogRead(VIBRATION_PIN);

  float flow = map(flowRaw, 0, 4095, 0, 60);
  float pressure = map(pressureRaw, 0, 4095, 0, 50) / 10.0;
  float vibration = map(vibrationRaw, 0, 4095, 0, 10);

  String status = "Healthy";
  if (flow > 35 && pressure < 1.5) status = "Possible Leak";
  if (flow > 45 && pressure < 1.0 && vibration > 7) status = "HIGH RISK";

  // Keep the Serial Monitor output for your Wokwi demonstration.
  Serial.println("--------------------------------");
  Serial.print("Flow: ");
  Serial.print(flow);
  Serial.println(" L/min");
  Serial.print("Pressure: ");
  Serial.print(pressure);
  Serial.println(" bar");
  Serial.print("Vibration: ");
  Serial.println(vibration);
  Serial.print("Status: ");
  Serial.println(status);

  // This JSON is read directly by the Hlokomela live IoT dashboard.
  char payload[220];
  snprintf(payload, sizeof(payload),
    "{\"deviceId\":\"PIPE-01\",\"flow\":%.1f,\"pressure\":%.2f,\"vibration\":%.1f,\"status\":\"%s\"}",
    flow, pressure, vibration, status.c_str());
  mqtt.publish(MQTT_TOPIC, payload);
  Serial.print("Published: ");
  Serial.println(payload);
  Serial.println("--------------------------------");

  delay(2000);
}
