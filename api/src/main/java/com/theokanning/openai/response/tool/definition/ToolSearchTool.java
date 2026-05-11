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
import java.util.Map;

/**
 * Tool definition for hosted or bring-your-own tool search.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolSearchTool implements ToolDefinition {

    /**
     * Tool type, always "tool_search".
     */
    @Builder.Default
    private String type = "tool_search";

    /**
     * Description shown to the model for client-executed tool search.
     */
    private String description;

    /**
     * Whether tool search is executed by the server or by the client.
     */
    private String execution;

    /**
     * Parameter schema for client-executed tool search.
     */
    private Object parameters;

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
