package com.theokanning.openai.upload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request to complete an Upload, assembling the uploaded Parts into a File.
 * <p>
 * https://platform.openai.com/docs/api-reference/uploads/complete
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompleteUploadRequest {

    /**
     * The ordered list of Part IDs. Optional: if omitted the server
     * assembles all uploaded parts ordered by part number.
     */
    @JsonProperty("part_ids")
    List<String> partIds;
}
