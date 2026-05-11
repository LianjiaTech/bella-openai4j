package com.theokanning.openai.response.tool.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Computer use tool definition for controlling virtual computer environments.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComputerUseTool implements ToolDefinition {

    /**
     * Tool type, always "computer_use_preview".
     */
    private String type = "computer_use_preview";

    /**
     * Display width in pixels.
     */
    @JsonProperty("display_width")
    private Integer displayWidth;

    /**
     * Display height in pixels.
     */
    @JsonProperty("display_height")
    private Integer displayHeight;

    /**
     * Environment type.
     */
    private String environment;
}
