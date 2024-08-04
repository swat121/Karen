package com.micro.service.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.dto.Client;
import com.micro.dto.mqtt.MqttCallbackData;
import com.micro.dto.mqtt.MqttCommandData;
import com.micro.service.BotService;
import com.micro.service.ClientService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class MqttService implements MqttCallback {

    private final MqttClient mqttClient;
    private final ClientService clientService;
    private final BotService botService;

    private final ObjectMapper objectMapper;

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

        if (topic.equalsIgnoreCase("home/esp/data/event")) {
            ObjectMapper mapper = new ObjectMapper();

            Client client = mapper.readValue(payload, Client.class);
            clientService.checkAndProcessClient(client);
            clientService.postBoardConfigInKarenData(client.getIp());

            String topicToSubscribe = String.format("home/esp/%s/event", client.getName());
            this.subscribe(topicToSubscribe);
        }

        if (topic.matches("home/esp/[^/]+/event") && !topic.equalsIgnoreCase("home/esp/data/event")) {
            MqttCallbackData callbackData = objectMapper.readValue(payload, MqttCallbackData.class);
            botService.sendCallback(callbackData);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOG.info("Delivery complete. Token: " + token);
    }

    public void publish(String topic, MqttCommandData payload) throws Exception {
        MqttMessage message = new MqttMessage(objectMapper.writeValueAsBytes(payload));
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