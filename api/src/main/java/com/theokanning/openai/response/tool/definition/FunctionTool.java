package com.theokanning.openai.response.tool.definition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Function tool definition for developer-defined custom functions.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionTool implements ToolDefinition {

    /**
     * Tool type, always "function".
     */
    private String type = "function";

    /**
     * Function name.
     */
    private String name;

    /**
     * Function description.
     */
    private String description;

    /**
     * JSON schema for function parameters.
     */
    private Map<String, Object> parameters;

    /**
     * Enforce strict parameter validation.
     */
    private Boolean strict;

    @Override
    public String getType() {
        return type;
    }
}

