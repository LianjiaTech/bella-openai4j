package com.theokanning.openai.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.theokanning.openai.response.content.ContentMessage;
import com.theokanning.openai.response.content.Reasoning;
import com.theokanning.openai.response.tool.CodeInterpreterToolCall;
import com.theokanning.openai.response.tool.ComputerToolCall;
import com.theokanning.openai.response.tool.CustomToolCall;
import com.theokanning.openai.response.tool.FileSearchToolCall;
import com.theokanning.openai.response.tool.FunctionToolCall;
import com.theokanning.openai.response.tool.ImageGenerationToolCall;
import com.theokanning.openai.response.tool.LocalShellToolCall;
import com.theokanning.openai.response.tool.MCPApprovalRequest;
import com.theokanning.openai.response.tool.MCPApprovalResponse;
import com.theokanning.openai.response.tool.MCPListTools;
import com.theokanning.openai.response.tool.MCPToolCall;
import com.theokanning.openai.response.tool.WebSearchToolCall;
import com.theokanning.openai.response.tool.output.ComputerToolCallOutput;
import com.theokanning.openai.response.tool.output.CustomToolCallOutput;
import com.theokanning.openai.response.tool.output.FunctionToolCallOutput;
import com.theokanning.openai.response.tool.output.LocalShellCallOutput;

/**
 * Base interface for all conversation items (both input and output). This includes input messages, response items, and tool call outputs.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = ContentMessage.class
)
@JsonSubTypes({
        // content message
        @JsonSubTypes.Type(value = ContentMessage.class, name = "message"),
        // Tool calls
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
        @JsonSubTypes.Type(value = CustomToolCall.class, name = "custom_tool_call"),
        // Other response items
        @JsonSubTypes.Type(value = Reasoning.class, name = "reasoning"),
        @JsonSubTypes.Type(value = ItemReference.class, name = "item_reference"),
        // Tool call outputs (ConversationItem but not ResponseItem)
        @JsonSubTypes.Type(value = FunctionToolCallOutput.class, name = "function_call_output"),
        @JsonSubTypes.Type(value = LocalShellCallOutput.class, name = "local_shell_call_output"),
        @JsonSubTypes.Type(value = ComputerToolCallOutput.class, name = "computer_call_output"),
        @JsonSubTypes.Type(value = CustomToolCallOutput.class, name = "custom_tool_call_output"),
        @JsonSubTypes.Type(value = MCPApprovalResponse.class, name = "mcp_approval_response")
})
public interface ConversationItem {

    /**
     * Get the object type for serialization.
     */
    String getType();
}
