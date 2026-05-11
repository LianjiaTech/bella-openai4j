package com.theokanning.openai.response.tool.definition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.theokanning.openai.assistants.assistant.Tool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Custom tool definition with flexible input formats.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomTool implements ToolDefinition {

    /**
     * Tool type, always "custom".
     */
    private String type = "custom";

    /**
     * Custom tool name.
     */
    private String name;

    /**
     * Tool description.
     */
    private String description;

    /**
     * Input format specification.
     */
    private Format format; // TextFormat or GrammarFormat


    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TextFormat.class, name = "text"),
            @JsonSubTypes.Type(value = GrammarFormat.class, name = "grammar"),
    })
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public interface Format {

        /**
         * Get the tool type.
         */
        String getType();
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextFormat implements Format {

        /**
         * Format type, always "text".
         */
        private String type = "text";
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarFormat implements Format {

        /**
         * Format type, always "grammar".
         */
        private String type = "grammar";

        /**
         * Grammar syntax type.
         */
        private String syntax; // "lark" or "regex"

        /**
         * Grammar definition.
         */
        private String definition;
    }

    @Override
    public Tool getRealTool() {
        return new Tool.Custom(this);
    }
}
