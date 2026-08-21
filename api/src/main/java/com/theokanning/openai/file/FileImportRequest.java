package com.theokanning.openai.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 从对象存储导入文件的请求。
 *
 * <p>服务端会根据 {@code bucket} 是否为空决定使用外部 bucket 或当前 purpose
 * 对应的 bucket，并将指定对象登记为文件节点。{@code spaceCode} 需要作为请求体
 * 字段传递，未指定时服务端回退到当前操作空间。</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileImportRequest {

    /**
     * 对象存储中的对象路径。
     */
    private String path;

    /**
     * 导入后的文件名。
     */
    private String filename;

    /**
     * 对象所在 bucket。为空时使用 purpose 对应的 bucket。
     */
    private String bucket;

    /**
     * 文件用途。
     */
    private String purpose;

    /**
     * 对象大小，服务端会校验其与实际对象大小一致。
     */
    private Long bytes;

    /**
     * 文件 MIME 类型。
     */
    private String mimeType;

    /**
     * 文件元数据。
     */
    private String metadata;

    /**
     * 文件所属空间。
     */
    private String spaceCode;

    /**
     * 导入文件的目标目录 ID。
     */
    private String ancestorId;

    /**
     * 文件描述。
     */
    private String description;

    /**
     * 文件所属城市列表。
     */
    private List<String> cities;

    /**
     * 文件标签列表。
     */
    private List<String> tags;
}
