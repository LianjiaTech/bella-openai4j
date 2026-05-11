package com.theokanning.openai.response.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents image input with configurable detail levels for vision capabilities.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputImage extends InputContent {

    /**
     * Content type, always "input_image".
     */
    @Builder.Default
    private String type = "input_image";

    /**
     * Controls the processing detail level.
     */
    private String detail;

    /**
     * Reference to uploaded file.
     */
    @JsonProperty("file_id")
    private String fileId;

    /**
     * Direct URL or base64 data URL.
     */
    @JsonProperty("image_url")
    private String imageUrl;

    @Override
    public String getType() {
        return type;
    }
}
