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
public class CreateResourceRequest {

    @JsonProperty("ancestor_id")
    private String ancestorId;

    private String name;

    @JsonProperty("resource_id")
    private String resourceId;

    private String purpose;
}
