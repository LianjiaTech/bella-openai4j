package com.theokanning.openai.upload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * The upload Part represents a chunk of bytes that belongs to an Upload.
 * <p>
 * https://platform.openai.com/docs/api-reference/uploads/part-object
 */
@Data
public class UploadPart {

    /**
     * The upload Part unique identifier, which can be referenced in API endpoints.
     */
    String id;

    /**
     * The object type, which is always "upload.part".
     */
    String object;

    /**
     * The ID of the Upload object that this Part was added to.
     */
    @JsonProperty("upload_id")
    String uploadId;

    /**
     * The Unix timestamp (in seconds) for when the Part was created.
     */
    @JsonProperty("created_at")
    Long createdAt;

    /**
     * The sequence number of this Part within the Upload.
     */
    @JsonProperty("part_number")
    Integer partNumber;

    /**
     * The size of this Part in bytes.
     */
    Long size;

    /**
     * The etag of this Part returned by the underlying storage.
     */
    String etag;
}
