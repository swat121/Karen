package com.micro.service.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.dto.Client;
import com.micro.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MqttReceiverService {
    private final ClientService clientService;
    private static final Logger LOG = LogManager.getRootLogger();
    private final MqttPahoMessageDrivenChannelAdapter adapter;
    private final Set<String> subscribedTopics = new HashSet<>();

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void processMessage(Message<?> message) {
        String topic = Objects.requireNonNull(message.getHeaders().get("mqtt_receivedTopic")).toString();
        String payload = message.getPayload().toString();
        LOG.info("Received MQTT message: " + payload + "  topic " + topic);

        if (topic.equals("home/esp/data/event")) {
            subscribedTopics.add("home/esp/data/event");

            ObjectMapper mapper = new ObjectMapper();

            try {
                Client client = mapper.readValue(payload, Client.class);
                clientService.checkAndProcessClient(client);
                clientService.postBoardConfigInKarenData(client.getIp());
                addSubscription("home/esp/" + client.getName() + "/event");
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
    }

    public void addSubscription(String topic) {
        LOG.info("Subscribe to topic: " + topic);
        if (!subscribedTopics.contains(topic)) {
            adapter.addTopic(topic);
            subscribedTopics.add(topic);
        }

        LOG.info("Subscribed topics: " + subscribedTopics);
    }

    public void removeSubscription(String topic) {
        LOG.info("Remove subscription to topic: " + topic);
        if (subscribedTopics.contains(topic)) {
            adapter.removeTopic(topic);
            subscribedTopics.remove(topic);
        }

        LOG.info("Subscribed topics: " + subscribedTopics);
    }
}
