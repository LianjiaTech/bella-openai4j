package com.theokanning.openai.response.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.theokanning.openai.response.ConversationItem;
import com.theokanning.openai.response.MessageRole;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base class for messages (both input and output).
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = MessageDeserializer.class)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "role"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = InputMessage.class, name = "user"),
        @JsonSubTypes.Type(value = InputMessage.class, name = "system"),
        @JsonSubTypes.Type(value = InputMessage.class, name = "developer"),
        @JsonSubTypes.Type(value = OutputMessage.class, name = "assistant")
})
public abstract class ContentMessage implements ConversationItem {

    /**
     * Message type, always "message".
     */
    protected String type = "message";

    /**
     * Message role.
     */
    protected MessageRole role;
}
