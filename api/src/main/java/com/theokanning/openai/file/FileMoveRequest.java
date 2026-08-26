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
public class FileMoveRequest {

    /**
     * 文件所属空间，仅用于生成 X-BELLA-SPACE-CODE 请求头。
     */
    @JsonIgnore
    private String spaceCode;

    @JsonProperty("file_id")
    private String fileId;

    @JsonProperty("ancestor_id")
    private String ancestorId;

    /**
     * 目标空间。为空时由服务端根据目标目录或文件当前空间推断。
     * 跨空间移动到目标空间根目录时，仅设置该字段即可。
     */
    @JsonProperty("target_space_code")
    private String targetSpaceCode;
}
