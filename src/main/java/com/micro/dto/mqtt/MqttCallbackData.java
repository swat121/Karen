package com.micro.dto.mqtt;

import com.micro.enums.ModuleTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MqttCallbackData {

    private String name;
    private ModuleTypes type;
    private String moduleName;
    private String status;
    private String moduleId;
    private String userId;
}
