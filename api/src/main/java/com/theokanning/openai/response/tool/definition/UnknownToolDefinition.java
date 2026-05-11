package com.theokanning.openai.response.tool.definition;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback for tool definitions whose type is not known by this SDK version.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnknownToolDefinition implements ToolDefinition {

    @Setter
    private String type;

    @JsonIgnore
    private Map<String, JsonNode> additionalProperties = new HashMap<>();

    @Override
    public String getType() {
        return type;
    }

    @JsonAnyGetter
    public Map<String, JsonNode> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, JsonNode> additionalProperties) {
        this.additionalProperties = additionalProperties == null ? new HashMap<>() : additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, JsonNode value) {
        if(additionalProperties == null) {
            additionalProperties = new HashMap<>();
        }
        additionalProperties.put(name, value);
    }
}
