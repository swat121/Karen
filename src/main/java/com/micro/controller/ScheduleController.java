package com.micro.controller;

import com.micro.dto.scheduler.IntervalTask;
import com.micro.dto.scheduler.PlannedTask;
import com.micro.service.DynamicSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleController {
    private final DynamicSchedulerService dynamicSchedulerService;

    @PostMapping("/api/v1/schedule/interval/start")
    public ResponseEntity<String> startIntervalTask(@RequestBody IntervalTask body) {
        dynamicSchedulerService.startIntervalTask(body).join();

        String responseMessage = String.format(
                "Interval task '%s' started with an update interval of %dms for client '%s' (%s: %s)",
                body.getTaskName(),
                body.getUpdateMillisTime(),
                body.getClientName(),
                body.getModule(),
                body.getModuleId()
        );

        return new ResponseEntity<>(responseMessage, HttpStatus.ACCEPTED);
    }

    @PostMapping("/api/v1/schedule/mqtt/interval/start")
    public ResponseEntity<String> startMqttIntervalTask() {
        dynamicSchedulerService.fetchConnectedClients("Mqtt-fetch", 3000L).join();

        return new ResponseEntity<>("Mqtt-fetch", HttpStatus.ACCEPTED);
    }

    @PostMapping("/api/v1/schedule/stop")
    public ResponseEntity<String> stopIntervalTask(@RequestParam String taskName) {
        dynamicSchedulerService.stopTask(taskName);
        return new ResponseEntity<>("Task: " + taskName + " stopped", HttpStatus.ACCEPTED);
    }

    @PostMapping("/api/v1/schedule/planned/start")
    public ResponseEntity<String> startPlannedTask(@RequestBody PlannedTask body) {
        dynamicSchedulerService.startPlannedTask(body);

        String responseMessage = String.format(
                "Planned task '%s' set for %02d:%02d for client '%s' (%s: %s)",
                body.getTaskName(),
                body.getHours(),
                body.getMinute(),
                body.getClientName(),
                body.getModule(),
                body.getModuleId()
        );

        return new ResponseEntity<>(responseMessage, HttpStatus.ACCEPTED);
    }

    @GetMapping("/api/v1/schedule/tasks")
    public List<String> getScheduleTasks() {
        return dynamicSchedulerService.getScheduledTasks().keySet().stream().toList();
    }
}
