package com.theokanning.openai.response.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for annotations that provide source references.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FileCitation.class, name = "file_citation"),
        @JsonSubTypes.Type(value = URLCitation.class, name = "url_citation"),
        @JsonSubTypes.Type(value = ContainerFileCitation.class, name = "container_file_citation"),
        @JsonSubTypes.Type(value = FilePath.class, name = "file_path")
})
public abstract class Annotation {

    /**
     * The type of annotation.
     */
    public abstract String getType();
}
