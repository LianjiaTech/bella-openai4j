package com.theokanning.openai.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.theokanning.openai.assistants.IUssrRequest;
import com.theokanning.openai.assistants.run.TruncationStrategy;
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
 * Request to create a new model response with optional tool usage and conversation context.
 *
 * @see <a href="https://platform.openai.com/docs/api-reference/response/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateResponseRequest implements IUssrRequest {

    /**
     * Model ID to use for generation.
     */
    private String model;

    /**
     * Text, image, or file input sent to the model.
     */
    @JsonSerialize(using = InputValue.InputValueSerializer.class)
    @JsonDeserialize(using = InputValue.InputValueDeserializer.class)
    private InputValue input;

    /**
     * System/developer message providing context.
     */
    private String instructions;

    /**
     * Whether to store the response for retrieval.
     */
    private Boolean store = true;

    /**
     * Enable real-time streaming.
     */
    private Boolean stream = false;

    /**
     * Conversation this response belongs to.
     */
    @JsonSerialize(using = ConversationValue.ConversationValueSerializer.class)
    @JsonDeserialize(using = ConversationValue.ConversationValueDeserializer.class)
    private ConversationValue conversation;

    /**
     * Previous response ID for continuing conversation.
     */
    @JsonProperty("previous_response_id")
    private String previousResponseId;

    /**
     * Cache key for system prompt.
     */
    @JsonProperty("prompt_cache_key")
    private String promptCacheKey;

    /**
     * Sampling temperature (0-2).
     */
    private Double temperature = 1.0;

    /**
     * Nucleus sampling parameter.
     */
    @JsonProperty("top_p")
    private Double topP = 1.0;

    /**
     * Maximum tokens to generate.
     */
    @JsonProperty("max_output_tokens")
    private Integer maxOutputTokens;

    /**
     * Maximum number of built-in tool calls allowed.
     */
    @JsonProperty("max_tool_calls")
    private Integer maxToolCalls;

    /**
     * Text response configuration.
     */
    private TextConfig text;

    /**
     * Tools available to the model.
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
     * Allow parallel tool execution.
     */
    @JsonProperty("parallel_tool_calls")
    private Boolean parallelToolCalls = true;

    /**
     * Reasoning model configuration.
     */
    private ReasoningConfig reasoning;

    /**
     * Run response in background.
     */
    private Boolean background = false;

    /**
     * Context truncation strategy.
     */
    @JsonProperty("truncation_strategy")
    private TruncationStrategy truncationStrategy;

    /**
     * The truncation strategy to use for the model response.
     * auto: If the input to this Response exceeds the model's context window size, the model will truncate the response to fit the context window by dropping items from the beginning of the conversation.
     * disabled (default): If the input size will exceed the context window size for a model, the request will fail with a 400 error.
     */
    private String truncation;

    /**
     * Additional data to include in response.
     */
    private List<String> include;

    /**
     * Response metadata key-value pairs.
     */
    private Map<String, String> metadata = new HashMap<>();

    private String user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TextConfig {
        /**
         * Response format (text, json_schema, json_object).
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReasoningConfig {
        /**
         * Reasoning effort level.
         */
        private String effort;

        /**
         * Reasoning summary level.
         */
        private String summary;
    }

}
