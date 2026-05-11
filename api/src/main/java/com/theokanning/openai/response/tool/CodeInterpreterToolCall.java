package com.theokanning.openai.response.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.theokanning.openai.response.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Executes Python code in a sandboxed environment.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeInterpreterToolCall extends ToolCall {

    /**
     * Tool call type, always "code_interpreter_call".
     */
    @Builder.Default
    private String type = "code_interpreter_call";

    /**
     * Unique identifier.
     */
    private String id;

    /**
     * Execution environment ID.
     */
    @JsonProperty("container_id")
    private String containerId;

    /**
     * Python code to execute.
     */
    private String code;

    /**
     * Execution outputs.
     */
    private List<CodeInterpreterOutput> outputs;

    /**
     * Execution status.
     */
    private ItemStatus status;

    @Override
    public String getType() {
        return type;
    }

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = CodeInterpreterOutputLogs.class, name = "logs"),
            @JsonSubTypes.Type(value = CodeInterpreterOutputImage.class, name = "image")
    })
    public abstract static class CodeInterpreterOutput {
        public abstract String getType();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeInterpreterOutputLogs extends CodeInterpreterOutput {
        private String type = "logs";
        private String logs;

        @Override
        public String getType() {
            return type;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeInterpreterOutputImage extends CodeInterpreterOutput {
        private String type = "image";
        private String url;

        @Override
        public String getType() {
            return type;
        }
    }
}
