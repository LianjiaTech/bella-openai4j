package com.theokanning.openai.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新文件或目录元数据的请求。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileMetadataUpdateRequest {

    /**
     * 文件或目录元数据。
     */
    private String metadata;
}
