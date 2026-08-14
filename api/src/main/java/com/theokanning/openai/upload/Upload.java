package com.theokanning.openai.upload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.file.File;

import lombok.Data;

/**
 * The Upload object can accept byte chunks in the form of Parts.
 * <p>
 * https://platform.openai.com/docs/api-reference/uploads
 */
@Data
public class Upload {

    /**
     * The Upload unique identifier, which can be referenced in API endpoints.
     */
    String id;

    /**
     * The object type, which is always "upload".
     */
    String object;

    /**
     * The name of the file to be uploaded.
     */
    String filename;

    /**
     * The intended purpose of the file.
     */
    String purpose;

    /**
     * The intended number of bytes to be uploaded.
     */
    Long bytes;

    /**
     * The status of the Upload: pending, completed, cancelled or expired.
     */
    String status;

    /**
     * The Unix timestamp (in seconds) for when the Upload was created.
     */
    @JsonProperty("created_at")
    Long createdAt;

    /**
     * The Unix timestamp (in seconds) for when the Upload will expire.
     */
    @JsonProperty("expires_at")
    Long expiresAt;

    /**
     * The ready File object after the Upload is completed.
     */
    File file;

    /**
     * The minimum size of a single part in bytes.
     */
    @JsonProperty("part_size_min")
    Long partSizeMin;

    /**
     * The maximum size of a single part in bytes.
     */
    @JsonProperty("part_size_max")
    Long partSizeMax;

    /**
     * The maximum number of parts allowed for this Upload.
     */
    @JsonProperty("max_parts")
    Integer maxParts;
}
