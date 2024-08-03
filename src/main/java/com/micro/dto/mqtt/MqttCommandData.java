package com.micro.dto.mqtt;

import com.micro.dto.data.CommandRequestData;
import com.micro.enums.ModuleTypes;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MqttCommandData extends CommandRequestData {
    private ModuleTypes type;

    public MqttCommandData(CommandRequestData commandRequestData, ModuleTypes type) {
        super(commandRequestData.getModuleName(), commandRequestData.getModuleId(),
                commandRequestData.getMode(), commandRequestData.getUserId());
        this.type = type;
    }
}
