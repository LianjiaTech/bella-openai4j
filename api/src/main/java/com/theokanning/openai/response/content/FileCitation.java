package com.theokanning.openai.response.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Citation reference to a file.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileCitation extends Annotation {

    /**
     * Annotation type, always "file_citation".
     */
    @Builder.Default
    private String type = "file_citation";

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
     * 切片Id
     */
    @JsonProperty("chunk_id")
    private String chunkId;

    /**
     * 分数
     */
    private Double score;

    /**
     * 标签
     */
    @JsonProperty("file_tag")
    private String fileTag;

    /**
     * Position in file list.
     */
    private Integer index;

    @Override
    public String getType() {
        return type;
    }
}
