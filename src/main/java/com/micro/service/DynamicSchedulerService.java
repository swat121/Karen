package com.micro.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.dto.data.SensorRequest;
import com.micro.dto.mqtt.MqttCommandData;
import com.micro.dto.mqtt.MqttResponse;
import com.micro.dto.scheduler.IntervalTask;
import com.micro.dto.scheduler.PlannedTask;
import com.micro.enums.Services;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

@Service
@EnableAsync
@EnableScheduling
@RequiredArgsConstructor
public class DynamicSchedulerService {

    private String mqttHost = "http://192.168.0.16:18083/api/v5/clients";
    private static final String API_V1_SENSORS = "/api/v1/sensors";
    private static final String KAREN_DATA = Services.KAREN_DATA.getTitle();
    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();
    private final BoardService boardService;
    private final ObjectMapper objectMapper;
    private final BotService botService;
    private final ConnectionService connectionService;
    private static final Logger LOG = LogManager.getRootLogger();

    private <T> HttpEntity<T> buildRequest(T data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(data, headers);
    }

    @Async
    public synchronized CompletableFuture<Void> startIntervalTask(IntervalTask body) {
        return CompletableFuture.runAsync(() -> {
            if (scheduledTasks.get(body.getTaskName()) != null) {
                stopTask(body.getTaskName());
            }
            ScheduledFuture<?> futureTask = null;

            futureTask = threadPoolTaskScheduler.scheduleWithFixedDelay(() -> executeSensorIntervalTask(body), body.getUpdateMillisTime());

            scheduledTasks.put(body.getTaskName(), futureTask);
        });
    }

    @Async
    public synchronized CompletableFuture<Void> fetchConnectedClients(String taskName, Long updateMillisTime) {
        return CompletableFuture.runAsync(() -> {
            if (scheduledTasks.get(taskName) != null) {
                stopTask(taskName);
            }
            ScheduledFuture<?> futureTask = null;

            futureTask = threadPoolTaskScheduler.scheduleWithFixedDelay(() -> {
               String response = connectionService.getResponseFromMqtt(mqttHost, String.class);
                try {
                    MqttResponse mqttResponse = objectMapper.readValue(response, MqttResponse.class);
                    LOG.info("Client from MQTT: " + mqttResponse);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            }, updateMillisTime);

            scheduledTasks.put(taskName, futureTask);
        });
    }

    public synchronized void stopTask(String taskName) {
        boolean cancelStatus;
        ScheduledFuture<?> existingTask = scheduledTasks.get(taskName);
        if (existingTask != null) {
            cancelStatus = existingTask.cancel(false);
            scheduledTasks.remove(taskName);
        } else {
            throw new IllegalArgumentException("Unknown task name: " + taskName);
        }
        LOG.info(String.format("Task: %s, is canceled: %b", taskName, cancelStatus));
    }

    public void startPlannedTask(PlannedTask body) {
        Date plannedDate = getPlannedDate(body.getHours(), body.getMinute());
        ScheduledFuture<?> futureTask = threadPoolTaskScheduler.schedule(
                () -> executeSwitcherPlannedTask(body),
                plannedDate
        );
        scheduledTasks.put(body.getTaskName(), futureTask);
    }

    private Date getPlannedDate(int hours, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    //TODO: fix scheduler
    private void executeSwitcherPlannedTask(PlannedTask body) {
//        String response = boardService.sendCommand(body.getClientName(), new MqttCommandData());
//        String message = String.format("Planned task %s is completed {%s}: client name - %s, module - %s, id - %s", body.getTaskName(), response, body.getClientName(), body.getModule(), body.getModuleId());
//        botService.notifyKarenBot(message, false);
//
//        scheduledTasks.remove(body.getTaskName());
//        LOG.info(String.format("Planned task: %s completed", body.getTaskName()));
    }

    private void executeSensorIntervalTask(IntervalTask body) {
//        String response = boardService.sendCommand(body.getClientName(), body.getModule(), body.getModuleId());
//        connectionService.postRequestForService(KAREN_DATA, API_V1_SENSORS, buildRequest(SensorRequest
//                .builder()
//                .name(body.getModule())
//                .sensorId(body.getModuleId())
//                .data(response)
//                .build()
//        ));
//        String message = String.format("Interval task %s is completed {%s}: client name - %s, module - %s, id - %s. All data saved in database", body.getTaskName(), response, body.getClientName(), body.getModule(), body.getModuleId());
//        botService.notifyKarenBot(message, false);
    }

    public Map<String, ScheduledFuture<?>> getScheduledTasks() {
        return this.scheduledTasks;
    }
}
