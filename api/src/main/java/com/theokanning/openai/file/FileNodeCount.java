package com.theokanning.openai.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 文件空间或目录下的节点统计结果。
 *
 * <p>统计结果按节点类型分别返回文件、目录和资源引用的数量。当查询指定
 * {@code ancestor_id} 时，只统计该目录的直接子节点，不递归统计子目录中的节点；
 * 未指定目录时，统计整个空间中的节点。</p>
 */
@Data
public class FileNodeCount {

    /**
     * 文件节点数量。
     */
    @JsonProperty("file_count")
    private long fileCount;

    /**
     * 目录节点数量。
     */
    @JsonProperty("directory_count")
    private long directoryCount;

    /**
     * 资源引用节点数量。
     */
    @JsonProperty("resource_count")
    private long resourceCount;
}
