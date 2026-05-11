package com.theokanning.openai.response.tool.definition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Comparison filter for file search operations.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonFilter {

    /**
     * Comparison operation type.
     */
    private String type; // "eq", "ne", "gt", "gte", "lt", "lte"

    /**
     * Key to compare against.
     */
    private String key;

    /**
     * Value for comparison.
     */
    private Object value; // String, Number, or Boolean
}
