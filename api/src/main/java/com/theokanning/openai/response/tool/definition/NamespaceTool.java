package com.theokanning.openai.response.tool.definition;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Groups function and custom tools under a shared namespace.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NamespaceTool implements ToolDefinition {

    /**
     * Tool type, always "namespace".
     */
    @Builder.Default
    private String type = "namespace";

    /**
     * Namespace name used in tool calls.
     */
    private String name;

    /**
     * Description shown to the model.
     */
    private String description;

    /**
     * Tools available inside the namespace.
     */
    private List<ToolDefinition> tools;

    @JsonIgnore
    @Builder.Default
    private Map<String, JsonNode> additionalProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, JsonNode> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, JsonNode value) {
        if(additionalProperties == null) {
            additionalProperties = new HashMap<>();
        }
        additionalProperties.put(name, value);
    }

    @Override
    public String getType() {
        return type;
    }
}
