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
public class DomTreeJsonRequest {

    @JsonProperty("file_id")
    private String fileId;

    @JsonProperty("dom_tree")
    private Object domTree;
}
