package com.micro.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfiguration {
    @Value("${mqtt.host}")
    private String MQTT_BROKER_URL;
    private static final String CLIENT_ID = "spring-boot-client";

    @Bean
    public MqttClient mqttClient() throws Exception {
        MqttClient client = new MqttClient(MQTT_BROKER_URL, CLIENT_ID, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        client.connect(options);
        return client;
    }
}
