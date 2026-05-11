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
 * Tool definition for applying unified diff patches.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplyPatchTool implements ToolDefinition {

    /**
     * Tool type, always "apply_patch".
     */
    @Builder.Default
    private String type = "apply_patch";

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
