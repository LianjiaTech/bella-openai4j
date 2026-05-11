package com.theokanning.openai.response.tool;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A request for human approval of a tool invocation.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MCPApprovalRequest extends ToolCall {

    /**
     * The type of the item. Always "mcp_approval_request".
     */
    @Builder.Default
    private String type = "mcp_approval_request";

    /**
     * The unique ID of the approval request.
     */
    private String id;

    /**
     * The name of the tool to run.
     */
    private String name;

    /**
     * The label of the MCP server making the request.
     */
    @JsonProperty("server_label")
    private String serverLabel;

    /**
     * A JSON string of arguments for the tool.
     */
    private String arguments;

    @Override
    public String getType() {
        return type;
    }
}
