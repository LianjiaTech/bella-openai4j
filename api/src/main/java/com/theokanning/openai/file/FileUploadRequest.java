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
public class FileUploadRequest {

    private String purpose;

    private String metadata;

    @JsonProperty("get_url")
    private Boolean getUrl;

    private Long expires;

    @JsonProperty("ancestor_id")
    private String ancestorId;

    private Boolean overwrite;

    private String description;

    private List<String> cities;

    private List<String> tags;
}
