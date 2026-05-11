package com.theokanning.openai.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represents a reference to a conversation in the Response API. Can be either a simple string ID or a detailed conversation object.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationReference {

    /**
     * The conversation ID.
     */
    private String id;

    /**
     * Unix timestamp when the conversation was created.
     */
    @JsonProperty("created_at")
    private Long createdAt;

    /**
     * Metadata key-value pairs for the conversation.
     */
    private Map<String, String> metadata;

    /**
     * Object type, always "conversation".
     */
    private String object;
}
