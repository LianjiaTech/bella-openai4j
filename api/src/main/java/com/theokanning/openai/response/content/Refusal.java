package com.theokanning.openai.response.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents model refusal to generate content.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refusal extends OutputContent {

    /**
     * Content type, always "refusal".
     */
    @Builder.Default
    private String type = "refusal";

    /**
     * Explanation for the refusal.
     */
    private String refusal;

    @Override
    public String getType() {
        return type;
    }
}
