package com.micro.dto.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommandRequestData {
    private String moduleName;
    private String moduleId;

    private String mode;
    private String userId;
}
