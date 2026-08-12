package com.theokanning.openai.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Progress {

    private String status;

    private String message;

    private Integer percent;

    @JsonProperty("progress_name")
    private String progressName;
}
