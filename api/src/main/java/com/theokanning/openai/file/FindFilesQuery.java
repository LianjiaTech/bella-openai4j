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
public class FindFilesQuery {

    @JsonProperty("space_code")
    private String spaceCode;

    @JsonProperty("ancestor_id")
    private String ancestorId;
}
