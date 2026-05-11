package com.theokanning.openai.response.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for input content types.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = InputText.class, name = "input_text"),
        @JsonSubTypes.Type(value = InputImage.class, name = "input_image"),
        @JsonSubTypes.Type(value = InputFile.class, name = "input_file"),
        @JsonSubTypes.Type(value = InputAudio.class, name = "input_audio")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class InputContent {

    /**
     * Content type identifier.
     */
    public abstract String getType();
}
