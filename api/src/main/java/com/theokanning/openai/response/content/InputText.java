package com.theokanning.openai.response.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents plain text input sent to the model.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputText extends InputContent {

    /**
     * Content type, always "input_text".
     */
    @Builder.Default
    private String type = "input_text";

    /**
     * The text content.
     */
    private String text;

    @Override
    public String getType() {
        return type;
    }
}
