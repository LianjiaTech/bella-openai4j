package com.theokanning.openai.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageFilesRequest {

    private Integer page;

    @JsonProperty("page_size")
    private Integer pageSize;

    @JsonProperty("ancestor_id")
    private String ancestorId;

    private String purpose;

    @JsonProperty("space_code")
    private String spaceCode;

    private String order;

    private String keyword;
}
