package com.micro.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.UUID;

@Configuration
@IntegrationComponentScan
public class MqttConfiguration {

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{"tcp://192.168.0.11:1883"});
//        options.setUserName("yourUsername"); // Если требуется
//        options.setPassword("yourPassword".toCharArray()); // Если требуется
        options.setKeepAliveInterval(60);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        return options;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        return factory;
    }

    // Канал для отправки сообщений
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    // Обработчик отправки сообщений
    @Bean
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler("karen-api-integration", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("karen.mqtt.esp.data");
        return messageHandler;
    }

    // Канал для получения сообщений
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    // Адаптер для получения сообщений
    @Bean
    public MqttPahoMessageDrivenChannelAdapter inbound() {
        String clientId = "karen-api-integration-" + UUID.randomUUID().toString();

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId, mqttClientFactory(),
                        "home/esp/data/event");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
}
