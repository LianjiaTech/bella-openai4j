package com.theokanning.openai.response.tool.definition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.theokanning.openai.assistants.assistant.Tool;

/**
 * Base interface for all tool definitions in the Response API.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FunctionTool.class, name = "function"),
        @JsonSubTypes.Type(value = FileSearchTool.class, name = "file_search"),
        @JsonSubTypes.Type(value = WebSearchTool.class, name = "web_search"),
        @JsonSubTypes.Type(value = WebSearchTool.class, name = "web_search_preview"),
        @JsonSubTypes.Type(value = ComputerUseTool.class, name = "computer_use_preview"),
        @JsonSubTypes.Type(value = CodeInterpreterTool.class, name = "code_interpreter"),
        @JsonSubTypes.Type(value = ImageGenerationTool.class, name = "image_generation"),
        @JsonSubTypes.Type(value = LocalShellTool.class, name = "local_shell"),
        @JsonSubTypes.Type(value = MCPTool.class, name = "mcp"),
        @JsonSubTypes.Type(value = CustomTool.class, name = "custom")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ToolDefinition {

    /**
     * Get the tool type.
     */
    String getType();

    @JsonIgnore
    default Tool getRealTool() {
        return null;
    }
}
