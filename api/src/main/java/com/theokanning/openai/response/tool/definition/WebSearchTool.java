package com.theokanning.openai.response.tool.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.assistants.assistant.Tool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Web search tool definition for searching the internet.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSearchTool implements ToolDefinition {

    /**
     * Tool type, "web_search" or "web_search_2025_08_26".
     */
    private String type = "web_search";

    /**
     * Search filters.
     */
    private SearchFilters filters;

    /**
     * User location for contextual search.
     */
    @JsonProperty("user_location")
    private UserLocation userLocation;

    @Override
    public Tool getRealTool() {
        Tool.WebSearch webSearch = new Tool.WebSearch();
        webSearch.setDefinition(this);
        return webSearch;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchFilters {
        /**
         * Allowed domains for search results.
         */
        @JsonProperty("allowed_domains")
        private List<String> allowedDomains;

        /**
         * Search context size.
         */
        @JsonProperty("search_context_size")
        private String searchContextSize; // "low", "medium", "high"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLocation {
        /**
         * Location type, always "approximate".
         */
        private String type = "approximate";

        /**
         * City name.
         */
        private String city;

        /**
         * ISO country code.
         */
        private String country;

        /**
         * Region.
         */
        private String region;

        /**
         * IANA timezone.
         */
        private String timezone;
    }
}
