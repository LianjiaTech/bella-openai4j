package com.theokanning.openai.response.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Path reference to a file.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilePath extends Annotation {

    /**
     * Annotation type, always "file_path".
     */
    @Builder.Default
    private String type = "file_path";

    /**
     * File ID.
     */
    @JsonProperty("file_id")
    private String fileId;

    /**
     * Index position.
     */
    private Integer index;

    @Override
    public String getType() {
        return type;
    }
}
