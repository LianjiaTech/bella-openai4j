package com.theokanning.openai.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request object for listing files with specific parameters
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileListRequest {

    /**
     * List of file IDs to retrieve
     */
    @JsonProperty("file_ids")
    private List<String> fileIds;

    /**
     * Whether to include file URLs in the response
     */
    @JsonProperty("get_url")
    private Boolean getUrl;

    /**
     * URL expiration time in seconds (only used when getUrl is true)
     */
    @JsonProperty("expires")
    private Long expires;
}