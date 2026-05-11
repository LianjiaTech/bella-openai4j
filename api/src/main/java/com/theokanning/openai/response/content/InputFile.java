package com.theokanning.openai.response.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents file input for document processing.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputFile extends InputContent {

    /**
     * Content type, always "input_file".
     */
    @Builder.Default
    private String type = "input_file";

    /**
     * Reference to uploaded file.
     */
    @JsonProperty("file_id")
    private String fileId;

    /**
     * Direct file URL.
     */
    @JsonProperty("file_url")
    private String fileUrl;

    /**
     * Raw file content.
     */
    @JsonProperty("file_data")
    private String fileData;

    /**
     * Display name for the file.
     */
    private String filename;

    @Override
    public String getType() {
        return type;
    }
}
