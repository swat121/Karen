package com.micro.service;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class MqttReceiver {

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void processMessage(Message<?> message) {
        System.out.println("Received MQTT message: " + message.getPayload());
    }
}
