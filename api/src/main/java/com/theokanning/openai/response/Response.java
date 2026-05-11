package com.theokanning.openai.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.theokanning.openai.CompletionTokensDetails;
import com.theokanning.openai.PromptTokensDetails;
import com.theokanning.openai.completion.chat.ChatResponseFormat;
import com.theokanning.openai.response.tool.definition.ToolDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A response from the OpenAI Response API containing generated content and context.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    /**
     * Unique response identifier.
     */
    private String id;

    /**
     * Object type, always "response".
     */
    private String object = "response";

    /**
     * Unix timestamp when created.
     */
    @JsonProperty("created_at")
    private Long createdAt;

    /**
     * Response status.
     */
    private ResponseStatus status;

    /**
     * Model used for generation.
     */
    private String model;

    /**
     * Generated content.
     */
    private List<ResponseItem> output;

    /**
     * Input context (if requested).
     */
    private List<ResponseItem> input;

    /**
     * Instructions.
     */
    @JsonSerialize(using = InstructionsValue.InstructionsValueSerializer.class)
    @JsonDeserialize(using = InstructionsValue.InstructionsValueDeserializer.class)
    private InstructionsValue instructions;

    /**
     * Conversation reference.
     */
    @JsonSerialize(using = ConversationValue.ConversationValueSerializer.class)
    @JsonDeserialize(using = ConversationValue.ConversationValueDeserializer.class)
    private ConversationValue conversation;

    /**
     * Previous response ID.
     */
    @JsonProperty("previous_response_id")
    private String previousResponseId;

    /**
     * Sampling temperature.
     */
    private Double temperature;

    /**
     * Nucleus sampling parameter.
     */
    @JsonProperty("top_p")
    private Double topP;

    /**
     * Maximum output tokens.
     */
    @JsonProperty("max_output_tokens")
    private Integer maxOutputTokens;

    /**
     * Text format configuration.
     */
    private TextFormat text;

    /**
     * Tool definitions.
     */
    private List<ToolDefinition> tools;

    /**
     * Tool choice strategy.
     */
    @JsonProperty("tool_choice")
    @JsonSerialize(using = ToolChoiceValue.ToolChoiceValueSerializer.class)
    @JsonDeserialize(using = ToolChoiceValue.ToolChoiceValueDeserializer.class)
    private ToolChoiceValue toolChoice;

    /**
     * Parallel tool calls enabled.
     */
    @JsonProperty("parallel_tool_calls")
    private Boolean parallelToolCalls;

    /**
     * Maximum tool calls.
     */
    @JsonProperty("max_tool_calls")
    private Integer maxToolCalls;

    /**
     * Reasoning configuration.
     */
    private ReasoningFormat reasoning;

    /**
     * Background execution.
     */
    private Boolean background;

    /**
     * Truncation strategy.
     */
    private String truncation;

    /**
     * Response metadata.
     */
    private Map<String, String> metadata = new HashMap<>();

    /**
     * Token usage information.
     */
    private Usage usage;

    /**
     * Error details if failed.
     */
    private ErrorDetails error;

    /**
     * Incomplete details if incomplete.
     */
    @JsonProperty("incomplete_details")
    private IncompleteDetails incompleteDetails;

    /**
     * Aggregated text output for convenience.
     */
    @JsonProperty("output_text")
    private String outputText;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextFormat {
        /**
         * Response format configuration.
         */
        @JsonSerialize(using = ChatResponseFormat.ChatResponseFormatSerializer.class)
        @JsonDeserialize(using = ChatResponseFormat.ChatResponseFormatDeserializer.class)
        private ChatResponseFormat format;

        /**
         * Response verbosity level.
         */
        private String verbosity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReasoningFormat {
        private String effort;
        private String summary;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetails {
        private String code;
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IncompleteDetails {
        private String reason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {

        @JsonProperty("input_tokens")
        long inputTokens;

        @JsonProperty("output_tokens")
        long outputTokens;

        @JsonProperty("total_tokens")
        long totalTokens;

        @JsonProperty("input_tokens_details")
        PromptTokensDetails inputTokensDetails;

        @JsonProperty("output_tokens_details")
        CompletionTokensDetails outputTokensDetails;

        public static Usage fromChatUsage(com.theokanning.openai.Usage usage) {
            if(usage == null) {
                return null;
            }
            return Usage.builder()
                    .inputTokens(usage.getPromptTokens())
                    .inputTokensDetails(usage.getPromptTokensDetails())
                    .outputTokens(usage.getCompletionTokens())
                    .outputTokensDetails(usage.getCompletionTokensDetails())
                    .totalTokens(usage.getTotalTokens())
                    .build();
        }
    }
}
