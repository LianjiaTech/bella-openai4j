package com.theokanning.openai.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新文件或目录创建人信息的请求。
 *
 * <p>至少设置一个字段。{@code createdAt} 使用 Unix 毫秒时间戳。</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileCreatorUpdateRequest {

    /**
     * 创建人 ID。
     */
    private Long cuid;

    /**
     * 创建人名称。
     */
    @JsonProperty("cu_name")
    private String cuName;

    /**
     * 创建时间，Unix 毫秒时间戳。
     */
    @JsonProperty("created_at")
    private Long createdAt;
}
