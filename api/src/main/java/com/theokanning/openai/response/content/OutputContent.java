package com.theokanning.openai.response.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for output content types.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OutputText.class, name = "output_text"),
        @JsonSubTypes.Type(value = Refusal.class, name = "refusal")
})
public abstract class OutputContent {

    /**
     * Content type identifier.
     */
    public abstract String getType();
}
