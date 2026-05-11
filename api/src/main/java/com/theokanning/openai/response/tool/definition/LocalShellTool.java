package com.theokanning.openai.response.tool.definition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.theokanning.openai.assistants.assistant.Tool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A tool that allows the model to execute shell commands in a local environment.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocalShellTool implements ToolDefinition {

    /**
     * The type of the local shell tool. Always "local_shell".
     */
    private String type = "local_shell";

    @Override
    public String getType() {
        return type;
    }


    @Override
    public Tool getRealTool() {
        return new Tool.LocalShell();
    }
}
