package com.micro.service;

import com.micro.dto.data.CommandRequestData;
import com.micro.dto.mqtt.MqttCommandData;
import com.micro.enums.ModuleTypes;
import com.micro.service.mqtt.MqttService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final MqttService mqttService;

    public void sendCommand(String name, CommandRequestData commandRequestData, ModuleTypes type) {
        String topic = String.format("home/esp/%s/command", name);
        try {
            mqttService.publish(topic, new MqttCommandData(commandRequestData, type));
        } catch (Exception e) {
            throw new RuntimeException("Failed to send command: " + e.getMessage(), e);
        }
    }
}
