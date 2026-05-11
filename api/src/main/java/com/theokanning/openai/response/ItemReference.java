package com.theokanning.openai.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An internal identifier for an item to reference.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemReference implements ResponseItem {

    /**
     * The type of item to reference. Always "item_reference".
     */
    private String type = "item_reference";

    /**
     * The ID of the item to reference.
     */
    private String id;

    @Override
    public String getType() {
        return type;
    }
}
