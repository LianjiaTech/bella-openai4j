package com.theokanning.openai.response.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Citation reference to a file within a container.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContainerFileCitation extends Annotation {

    /**
     * Annotation type, always "container_file_citation".
     */
    @Builder.Default
    private String type = "container_file_citation";

    /**
     * Container ID.
     */
    @JsonProperty("container_id")
    private String containerId;

    /**
     * File ID.
     */
    @JsonProperty("file_id")
    private String fileId;

    /**
     * File name.
     */
    private String filename;

    /**
     * Character start position.
     */
    @JsonProperty("start_index")
    private Integer startIndex;

    /**
     * Character end position.
     */
    @JsonProperty("end_index")
    private Integer endIndex;

    @Override
    public String getType() {
        return type;
    }
}
