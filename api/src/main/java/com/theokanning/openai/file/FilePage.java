package com.theokanning.openai.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FilePage {

    private Integer page;

    @JsonProperty("page_size")
    private Integer pageSize;

    private Integer limit;

    private Integer total;

    private List<File> data;

    @JsonProperty("has_more")
    private Boolean hasMore;
}
