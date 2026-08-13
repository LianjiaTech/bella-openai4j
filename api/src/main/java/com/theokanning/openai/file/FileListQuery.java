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
public class FileListQuery {

    /**
     * 文件所属空间，仅用于生成 X-BELLA-SPACE-CODE 请求头。
     */
    @JsonIgnore
    private String spaceCode;

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
