package com.theokanning.openai.response.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.response.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Searches uploaded files for relevant content.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSearchToolCall extends ToolCall {

    /**
     * Tool call type, always "file_search_call".
     */
    @Builder.Default
    private String type = "file_search_call";

    /**
     * Unique identifier.
     */
    private String id;

    /**
     * Search queries executed.
     */
    private List<String> queries;

    /**
     * Execution status.
     */
    private ItemStatus status;

    /**
     * Search results.
     */
    private List<SearchResult> results;

    @Override
    public String getType() {
        return type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResult {

        /**
         * File ID.
         */
        @JsonProperty("file_id")
        private String fileId;

        /**
         * File name.
         */
        private String filename;

        /**
         * Retrieved text content.
         */
        private String text;

        /**
         * Relevance score (0-1).
         */
        private double score;

        /**
         * Additional attributes.
         */
        private Map<String, Object> attributes;
    }
}
