package com.theokanning.openai.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class MkdirRequest {

    /**
     * 文件所属空间，仅用于生成 X-BELLA-SPACE-CODE 请求头。
     */
    @JsonIgnore
    private String spaceCode;

    @JsonProperty("ancestor_id")
    private String ancestorId;

    private String name;

    private String description;

    private String purpose;

    /**
     * 目录元数据。
     */
    private String metadata;

    /**
     * 目录所属城市列表。
     */
    private List<String> cities;

    /**
     * 目录标签列表。
     */
    private List<String> tags;
}
