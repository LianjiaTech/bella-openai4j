package com.theokanning.openai.response.content;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.response.ItemStatus;
import com.theokanning.openai.response.ResponseItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Contains reasoning model's chain of thought.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reasoning implements ResponseItem {

    /**
     * Reasoning type, always "reasoning".
     */
    @Builder.Default
    private String type = "reasoning";

    /**
     * Unique identifier.
     */
    private String id;

    /**
     * Reasoning summary.
     */
    private List<SummaryText> summary;

    /**
     * Detailed reasoning content.
     */
    private List<ReasoningText> content;

    /**
     * Encrypted reasoning for stateless usage.
     */
    @JsonProperty("encrypted_content")
    private String encryptedContent;

    /**
     * Status of the reasoning.
     */
    private ItemStatus status;

    @Override
    public String getType() {
        return type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryText {
        @Builder.Default
        private String type = "summary_text";
        private String text;
        @JsonIgnore
        private String reasoningSignature;
        @JsonIgnore
        private String redactedReasoningContent;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReasoningText {
        @Builder.Default
        private String type = "reasoning_text";
        private String text;
        @JsonIgnore
        private String reasoningSignature;
        @JsonIgnore
        private String redactedReasoningContent;
    }
}
