package com.theokanning.openai.response.tool.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Code interpreter tool definition for executing Python code.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeInterpreterTool implements ToolDefinition {

    /**
     * Tool type, always "code_interpreter".
     */
    private String type = "code_interpreter";

    /**
     * Container configuration (String container ID or ContainerConfig).
     */
    private Object container; // String or ContainerConfig

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContainerConfig {
        /**
         * Container type, always "auto".
         */
        private String type = "auto";

        /**
         * Files to make available in the container.
         */
        @JsonProperty("file_ids")
        private List<String> fileIds;
    }
}
