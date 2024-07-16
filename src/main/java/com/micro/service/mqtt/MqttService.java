package com.micro.service.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Service
public class MqttService implements MqttCallback {

    @Autowired
    private MqttClient mqttClient;

    private Set<String> connectedDevices = new HashSet<>();

    @PostConstruct
    public void init() {
        try {
            mqttClient.setCallback(this);
            if (!mqttClient.isConnected()) {
                mqttClient.connect();
            }
//            mqttClient.subscribe("$SYS/brokers/+/clients/connected");
//            mqttClient.subscribe("$SYS/brokers/+/clients/disconnected");
            mqttClient.subscribe("home/esp/data/event");
        } catch (MqttException e) {
            System.err.println("Error during MQTT subscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        System.out.println("Message arrived. Topic: " + topic + " Message: " + payload);

        if (topic.contains("connected")) {
            String clientId = extractClientId(payload);
            connectedDevices.add(clientId);
            System.out.println("Device connected: " + clientId);
        } else if (topic.contains("disconnected")) {
            String clientId = extractClientId(payload);
            connectedDevices.remove(clientId);
            System.out.println("Device disconnected: " + clientId);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Delivery complete. Token: " + token);
    }

    public void publish(String topic, String payload) throws Exception {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        mqttClient.publish(topic, message);
    }

    public void subscribe(String topic) throws Exception {
        mqttClient.subscribe(topic);
    }

    public Set<String> getConnectedDevices() {
        return connectedDevices;
    }

    private String extractClientId(String payload) {
        // Логика извлечения clientId из payload. Это может зависеть от формата сообщения.
        // Пример: { "clientid": "your_client_id" }
        // Вы можете использовать библиотеку JSON для разбора сообщения, например, org.json или Jackson
        return payload;
    }
}