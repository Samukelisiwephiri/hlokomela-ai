# Connecting a Wokwi simulation to Hlokomela AI

1. Open `iot-dashboard.html` in the website and choose a unique MQTT topic, for example `hlokomela/team-name/pipe-01`.
2. Put the same topic in `wokwi-sensor-example.ino` as `MQTT_TOPIC`.
3. In Wokwi, install the **PubSubClient** library, then run the sketch on an ESP32.
4. Click **Connect live feed** in the dashboard. The browser must use the broker's WebSocket URL; the ESP32 sketch must use the same broker's normal MQTT TCP host and port.

The included demo uses the EMQX public broker only for a short prototype. Pick a private, authenticated MQTT broker before sharing a pilot with a municipality.

Wokwi’s ESP32 simulator supports internet access and MQTT, so it can publish sensor values to an external broker. See the official [Wokwi ESP32 WiFi guide](https://docs.wokwi.com/guides/esp32-wifi) for current connectivity details.
