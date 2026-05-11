package com.theokanning.openai.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.theokanning.openai.response.content.OutputMessage;
import com.theokanning.openai.response.content.Reasoning;
import com.theokanning.openai.response.tool.CodeInterpreterToolCall;
import com.theokanning.openai.response.tool.ComputerToolCall;
import com.theokanning.openai.response.tool.CustomToolCall;
import com.theokanning.openai.response.tool.FileSearchToolCall;
import com.theokanning.openai.response.tool.FunctionToolCall;
import com.theokanning.openai.response.tool.ImageGenerationToolCall;
import com.theokanning.openai.response.tool.LocalShellToolCall;
import com.theokanning.openai.response.tool.MCPApprovalRequest;
import com.theokanning.openai.response.tool.MCPListTools;
import com.theokanning.openai.response.tool.MCPToolCall;
import com.theokanning.openai.response.tool.WebSearchToolCall;

/**
 * Base interface for response output items only. InputMessage is not a ResponseItem since it's input data, not response output. Only OutputMessage
 * (assistant role) and tool calls are ResponseItems. ResponseItem extends ConversationItem.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OutputMessage.class, name = "message"),
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
        @JsonSubTypes.Type(value = ItemReference.class, name = "item_reference")
})
public interface ResponseItem extends ConversationItem {
    String getId();
}
