package com.theokanning.openai.response.tool.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.assistants.assistant.Tool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * MCP (Model Context Protocol) tool definition for external system integration.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MCPTool implements ToolDefinition {

    /**
     * Tool type, always "mcp".
     */
    private String type = "mcp";

    /**
     * MCP server label/identifier.
     */
    @JsonProperty("server_label")
    private String serverLabel;

    /**
     * Custom MCP server URL.
     */
    @JsonProperty("server_url")
    private String serverUrl;

    /**
     * Predefined connector ID.
     */
    @JsonProperty("connector_id")
    private String connectorId;

    /**
     * OAuth authorization token.
     */
    private String authorization;

    /**
     * Custom HTTP headers.
     */
    private Map<String, String> headers;

    /**
     * Server description.
     */
    @JsonProperty("server_description")
    private String serverDescription;

    /**
     * Allowed tools configuration.
     */
    @JsonProperty("allowed_tools")
    private Object allowedTools; // List<String> or MCPToolFilter

    /**
     * Approval requirements.
     */
    @JsonProperty("require_approval")
    private Object requireApproval; // "always", "never", or MCPToolApprovalFilter

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MCPToolFilter {

        /**
         * Only allow read-only tools.
         */
        @JsonProperty("read_only")
        private Boolean readOnly;

        /**
         * Specific tool names to allow.
         */
        @JsonProperty("tool_names")
        private List<String> toolNames;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MCPToolApprovalFilter {

        /**
         * Tools that always require approval.
         */
        private MCPToolFilter always;

        /**
         * Tools that never require approval.
         */
        private MCPToolFilter never;
    }

    @Override
    public Tool getRealTool() {
        return new Tool.MCP(this);
    }

    public boolean allowed(String toolName) {
        if(allowedTools == null) {
            return true;
        }
        if(allowedTools instanceof List) {
            return ((List<?>) allowedTools).contains(toolName);
        }
        if(allowedTools instanceof MCPToolFilter) {
            MCPToolFilter filter = (MCPToolFilter) allowedTools;
            return filter.getToolNames() == null || filter.getToolNames().contains(toolName);
        }
        return true;
    }

    public boolean needApproval(String toolName) {
        if(requireApproval == null) {
            return false;
        }
        if(requireApproval instanceof String) {
            if("always".equalsIgnoreCase((String) requireApproval)) {
                return true;
            }
            return false;
        }
        if(requireApproval instanceof MCPToolApprovalFilter) {
            MCPToolFilter always = ((MCPToolApprovalFilter) requireApproval).getAlways();
            if(always == null) {
                return false;
            } else {
                return always.getToolNames() != null && always.getToolNames().contains(toolName);
            }
        }
        return false;
    }
}
