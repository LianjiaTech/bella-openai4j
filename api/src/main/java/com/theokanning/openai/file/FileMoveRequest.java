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
public class FileMoveRequest {

    @JsonProperty("file_id")
    private String fileId;

    @JsonProperty("ancestor_id")
    private String ancestorId;
}
