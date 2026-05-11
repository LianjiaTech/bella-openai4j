package com.theokanning.openai.response.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents model-generated text with optional annotations.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutputText extends OutputContent {

    /**
     * Content type, always "output_text".
     */
    @Builder.Default
    private String type = "output_text";

    /**
     * Generated text content.
     */
    private String text;

    /**
     * Reference annotations.
     */
    private List<Annotation> annotations;

    /**
     * Optional log probabilities.
     */
    private List<LogProb> logprobs;

    @Override
    public String getType() {
        return type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LogProb {
        private int[] bytes;
        private double logprob;
        private String token;
        @JsonProperty("top_logprobs")
        private List<TopLogProb> topLogprobs;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopLogProb {
        private int[] bytes;
        private double logprob;
        private String token;
    }
}
