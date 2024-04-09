package com.micro.service;

import lombok.RequiredArgsConstructor;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MqttSenderService {

    private final MessageChannel mqttOutboundChannel;

    public void sendMessage(String data) {
        mqttOutboundChannel.send(MessageBuilder.withPayload(data).build());
    }
}
