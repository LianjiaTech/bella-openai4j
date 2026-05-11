package com.theokanning.openai.response.tool;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.theokanning.openai.response.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Searches the internet for information to include in responses.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSearchToolCall extends ToolCall {

    /**
     * Tool call type, always "web_search_call".
     */
    @Builder.Default
    private String type = "web_search_call";

    /**
     * Unique identifier.
     */
    private String id;

    /**
     * Execution status.
     */
    private ItemStatus status;

    /**
     * Specific action performed.
     */
    private SearchAction action;

    @Override
    public String getType() {
        return type;
    }

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = SearchAction.class, name = "search"),
            @JsonSubTypes.Type(value = OpenPageAction.class, name = "open_page"),
            @JsonSubTypes.Type(value = FindAction.class, name = "find")
    })
    public abstract static class SearchActionBase {
        public abstract String getType();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchAction extends SearchActionBase {
        private String type = "search";
        private String query;
        private List<SearchSource> sources;

        @Override
        public String getType() {
            return type;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpenPageAction extends SearchActionBase {
        private String type = "open_page";
        private String url;

        @Override
        public String getType() {
            return type;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindAction extends SearchActionBase {
        private String type = "find";
        private String pattern;
        private String url;

        @Override
        public String getType() {
            return type;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchSource {
        private String type = "url";
        private String url;
    }
}
