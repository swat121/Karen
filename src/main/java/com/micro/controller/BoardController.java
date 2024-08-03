package com.micro.controller;


import com.micro.dto.data.CommandRequestData;
import com.micro.enums.ModuleTypes;
import com.micro.service.BoardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v2")
public class BoardController {
    public final BoardService boardService;

    @GetMapping("/{name}/sensors")
    public ResponseEntity<String> getDataBySensor(@PathVariable String name, @RequestBody CommandRequestData commandRequestData){
        boardService.sendCommand(name, commandRequestData, ModuleTypes.SENSOR);

        String text = String.format("Processing started for: %s, module: %s", name, commandRequestData.getModuleName());
        return ResponseEntity.ok(text);
    }

    @GetMapping("/{name}/trackers")
    public ResponseEntity<String> getDataByTracker(@PathVariable String name, @RequestBody CommandRequestData commandRequestData){
        boardService.sendCommand(name, commandRequestData, ModuleTypes.TRACKER);

        String text = String.format("Processing started for: %s, module: %s", name, commandRequestData.getModuleName());
        return ResponseEntity.ok(text);
    }

    @PutMapping("/{name}/switchers")
    public ResponseEntity<String> putDataBySwitcher(@PathVariable String name, @RequestBody CommandRequestData commandRequestData){
        boardService.sendCommand(name, commandRequestData, ModuleTypes.SWITCHER);

        String text = String.format("Processing started for: %s, module: %s", name, commandRequestData.getModuleName());
        return ResponseEntity.ok(text);
    }
}

