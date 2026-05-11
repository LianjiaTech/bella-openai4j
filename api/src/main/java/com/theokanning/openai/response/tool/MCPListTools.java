package com.theokanning.openai.response.tool;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * A list of tools available on an MCP server.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MCPListTools extends ToolCall {

    /**
     * The type of the item. Always "mcp_list_tools".
     */
    @Builder.Default
    private String type = "mcp_list_tools";

    /**
     * The unique ID of the list.
     */
    private String id;

    /**
     * The label of the MCP server.
     */
    @JsonProperty("server_label")
    private String serverLabel;

    /**
     * The tools available on the server.
     */
    private List<MCPToolInfo> tools;

    /**
     * Error message if the server could not list tools.
     */
    private String error;

    @Override
    public String getType() {
        return type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MCPToolInfo {
        /**
         * The name of the tool.
         */
        private String name;

        /**
         * The description of the tool.
         */
        private String description;

        /**
         * The JSON schema describing the tool's input.
         */
        @JsonProperty("input_schema")
        private Map<String, Object> inputSchema;

        /**
         * Additional annotations about the tool.
         */
        private Map<String, Object> annotations;
    }
}
