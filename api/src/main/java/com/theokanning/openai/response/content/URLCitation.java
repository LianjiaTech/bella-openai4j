package com.theokanning.openai.response.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Citation reference to a web URL.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class URLCitation extends Annotation {

    /**
     * Annotation type, always "url_citation".
     */
    @Builder.Default
    private String type = "url_citation";

    /**
     * Web page URL.
     */
    private String url;

    /**
     * Web page title.
     */
    private String title;

    /**
     * Character start position in text.
     */
    @JsonProperty("start_index")
    private Integer startIndex;

    /**
     * Character end position in text.
     */
    @JsonProperty("end_index")
    private Integer endIndex;

    @Override
    public String getType() {
        return type;
    }
}
