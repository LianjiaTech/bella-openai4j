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
public class FileAncestorIdsRequest {

    @JsonProperty("space_code")
    private String spaceCode;

    @JsonProperty("file_ids")
    private List<String> fileIds;
}
