package com.micro.service.mqtt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger LOG = LogManager.getRootLogger();

    private Set<String> connectedDevices = new HashSet<>();

    @PostConstruct
    public void init() {
        try {
            mqttClient.setCallback(this);
            if (!mqttClient.isConnected()) {
                mqttClient.connect();
            }
            mqttClient.subscribe("home/esp/data/event");
        } catch (MqttException e) {
            LOG.error("Error during MQTT subscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        LOG.info("Connection lost: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        LOG.info("Message arrived. Topic: " + topic + " Message: " + payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOG.info("Delivery complete. Token: " + token);
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
}