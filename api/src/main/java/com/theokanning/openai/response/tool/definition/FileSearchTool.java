package com.theokanning.openai.response.tool.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.assistants.assistant.Tool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * File search tool definition for searching uploaded files.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSearchTool implements ToolDefinition {

    /**
     * Tool type, always "file_search".
     */
    private String type = "file_search";

    /**
     * Vector store IDs to search.
     */
    @JsonProperty("vector_store_ids")
    private List<String> vectorStoreIds;

    /**
     * Search filters.
     */
    private Object filters; // ComparisonFilter or CompoundFilter - can be either type

    /**
     * Maximum number of results (1-50).
     */
    @JsonProperty("max_num_results")
    private Integer maxNumResults;

    /**
     * Ranking options.
     */
    @JsonProperty("ranking_options")
    private RankingOptions rankingOptions;

    @Override
    public Tool getRealTool() {
        Tool.Retrieval retrieval = new Tool.Retrieval();
        Tool.DefaultMetadata metadata = new Tool.DefaultMetadata();
        if(maxNumResults != null) {
            metadata.setTopK(maxNumResults);
        }
        if(rankingOptions != null && rankingOptions.scoreThreshold != null) {
            metadata.setScore(rankingOptions.scoreThreshold);
        }
        retrieval.setFileIds(vectorStoreIds);
        retrieval.setDefaultMetadata(metadata);
        return retrieval;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankingOptions {
        /**
         * Ranker to use.
         */
        private String ranker;

        /**
         * Score threshold (0-1).
         */
        @JsonProperty("score_threshold")
        private Double scoreThreshold;
    }
}
