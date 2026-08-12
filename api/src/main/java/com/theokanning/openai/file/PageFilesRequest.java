package com.theokanning.openai.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageFilesRequest {

    private Integer page;

    @JsonProperty("page_size")
    private Integer pageSize;

    @JsonProperty("space_code")
    private String spaceCode;

    @JsonProperty("ancestor_id")
    private String ancestorId;

    private String purpose;

    private List<String> tags;

    private List<String> cities;

    private Long cuid;

    private Long muid;

    private String filename;

    private String type;

    @JsonProperty("file_id")
    private String fileId;

    private String extension;

    private String order;
}
