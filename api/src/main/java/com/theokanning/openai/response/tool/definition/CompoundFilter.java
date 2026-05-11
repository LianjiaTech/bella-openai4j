package com.theokanning.openai.response.tool.definition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Compound filter for combining multiple filters with logical operations.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompoundFilter {

    /**
     * Logical operation type.
     */
    private String type; // "and" or "or"

    /**
     * List of filters to combine.
     */
    private List<Object> filters; // Array<ComparisonFilter | CompoundFilter>
}
