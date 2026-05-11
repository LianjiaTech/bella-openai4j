package com.theokanning.openai.response.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.response.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a call to a developer-defined custom function.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionToolCall extends ToolCall {

    /**
     * Tool call type, always "function_call".
     */
    @Builder.Default
    private String type = "function_call";

    /**
     * Unique identifier.
     */
    private String id;

    /**
     * Call ID for mapping to output.
     */
    @JsonProperty("call_id")
    private String callId;

    /**
     * Function name to execute.
     */
    private String name;

    /**
     * Function arguments as JSON string.
     */
    private String arguments;

    /**
     * Status of the function call.
     */
    private ItemStatus status;

    @Override
    public String getType() {
        return type;
    }
}
