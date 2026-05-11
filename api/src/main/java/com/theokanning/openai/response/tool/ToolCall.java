package com.theokanning.openai.response.tool;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.theokanning.openai.response.ResponseItem;

/**
 * Base class for all tool calls in the Response API.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FunctionToolCall.class, name = "function_call"),
        @JsonSubTypes.Type(value = FileSearchToolCall.class, name = "file_search_call"),
        @JsonSubTypes.Type(value = WebSearchToolCall.class, name = "web_search_call"),
        @JsonSubTypes.Type(value = CodeInterpreterToolCall.class, name = "code_interpreter_call"),
        @JsonSubTypes.Type(value = LocalShellToolCall.class, name = "local_shell_call"),
        @JsonSubTypes.Type(value = ComputerToolCall.class, name = "computer_call"),
        @JsonSubTypes.Type(value = ImageGenerationToolCall.class, name = "image_generation_call"),
        @JsonSubTypes.Type(value = MCPToolCall.class, name = "mcp_call"),
        @JsonSubTypes.Type(value = MCPListTools.class, name = "mcp_list_tools"),
        @JsonSubTypes.Type(value = MCPApprovalRequest.class, name = "mcp_approval_request"),
        @JsonSubTypes.Type(value = CustomToolCall.class, name = "custom_tool_call")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ToolCall implements ResponseItem {
    public abstract String getId();
    public abstract void setId(String id);
}
