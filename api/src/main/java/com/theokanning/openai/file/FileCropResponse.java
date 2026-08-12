package com.theokanning.openai.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FileCropResponse {

    /**
     * The cropped image as a Data URI (e.g. data:image/png;base64,...)
     */
    @JsonProperty("image_base64")
    private String imageBase64;
}
