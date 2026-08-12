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
public class FileListQuery {

    @JsonProperty("ancestor_id")
    private String ancestorId;

    private String purpose;

    private Integer limit;

    private String order;

    private String after;

    @JsonProperty("get_url")
    private Boolean getUrl;

    private Long expires;
}
