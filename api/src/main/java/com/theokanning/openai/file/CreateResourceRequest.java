package com.theokanning.openai.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    /**
     * 文件所属空间，仅用于生成 X-BELLA-SPACE-CODE 请求头。
     */
    @JsonIgnore
    private String spaceCode;

    @JsonProperty("ancestor_id")
    private String ancestorId;

    private String name;

    @JsonProperty("resource_id")
    private String resourceId;

    private String purpose;
}
