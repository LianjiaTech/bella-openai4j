package com.theokanning.openai.space;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpaceOperator {

    private Long userId;

    private String userName;

    private String email;

    private String tenantId;

    private String spaceCode;

    private String source;

    private String sourceId;

    private String managerAk;

    private Map<String, Object> optionalInfo;
}
