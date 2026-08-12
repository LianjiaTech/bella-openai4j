package com.theokanning.openai.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Progress {

    private Long id;

    @JsonProperty("file_id")
    private String fileId;

    private String name;

    private String status;

    private String message;

    private Integer percent;

    private Long cuid;

    @JsonProperty("cu_name")
    private String cuName;

    private Long ctime;

    private Long muid;

    @JsonProperty("mu_name")
    private String muName;

    private Long mtime;
}
