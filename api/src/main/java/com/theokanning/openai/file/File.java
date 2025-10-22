package com.theokanning.openai.file;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * A file uploaded to OpenAi
 * <p>
 * https://beta.openai.com/docs/api-reference/files
 */
@Data
public class File {

    /**
     * The unique id of this file.
     */
    String id;

    /**
     * The type of object returned, should be "file".
     */
    String object;

    /**
     * File size in bytes.
     */
    Long bytes;

    /**
     * The creation time in epoch seconds.
     */
    @JsonProperty("created_at")
    Long createdAt;

    /**
     * The name of the file.
     */
    String filename;

    /**
     * Description of the file's purpose.
     */
    String purpose;

    /**
     * The current status of the file, which can be either uploaded, processed, pending, error, deleting or deleted.
     * @deprecated  The current status of the file, which can be either uploaded, processed, or error.
     */
    @Deprecated
    String status;

    /**
     * Additional details about the status of the file.
     * If the file is in the error state, this will include a message describing the error.
     * @deprecated  For details on why a fine-tuning training file failed validation, see the error field on fine_tuning.job.
     */
    @JsonProperty("status_details")
    @Deprecated
    String statusDetails;

    String url;
    /**
     * subtype of mime_type. eg: image/jpeg
     */
    @JsonProperty("mime_type")
    String mimeType;
    /**
     * type of mime_type. eg: image
     */
    String type;
    String extension;
    @JsonProperty("is_dir")
    Boolean isDir;
    @JsonProperty("dom_tree_file_id")
    String domTreeFileId;
    @JsonProperty("pdf_file_id")
    String pdfFileId;
    Integer version;
    @JsonProperty("space_code")
    String spaceCode;
    Long cuid;
    @JsonProperty("cu_name")
    String cuName;
    Long ctime;
    Long muid;
    @JsonProperty("mu_name")
    String muName;
    Long mtime;
    String description;
}
