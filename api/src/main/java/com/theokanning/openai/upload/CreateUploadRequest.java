package com.theokanning.openai.upload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request to create an Upload object to which Parts can be added.
 * Once completed, the Upload contains a ready-to-use File object.
 * <p>
 * https://platform.openai.com/docs/api-reference/uploads/create
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateUploadRequest {

    /**
     * The name of the file to upload.
     */
    String filename;

    /**
     * The intended purpose of the uploaded file.
     */
    String purpose;

    /**
     * The total number of bytes in the file you are uploading.
     */
    Long bytes;

    /**
     * The MIME type of the file, e.g. "text/plain".
     */
    @JsonProperty("mime_type")
    String mimeType;

    /**
     * The space to upload the file into (bella extension).
     */
    @JsonProperty("space_code")
    String spaceCode;

    /**
     * The parent directory file id (bella extension).
     */
    @JsonProperty("ancestor_id")
    String ancestorId;

    /**
     * Custom metadata attached to the resulting file (bella extension).
     */
    String metadata;

    /**
     * Description of the resulting file (bella extension).
     */
    String description;

    /**
     * Cities associated with the resulting file (bella extension).
     */
    List<String> cities;

    /**
     * Tags associated with the resulting file (bella extension).
     */
    List<String> tags;
}
