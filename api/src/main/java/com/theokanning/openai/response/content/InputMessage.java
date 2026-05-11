package com.theokanning.openai.response.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.theokanning.openai.response.ConversationItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Messages sent to the model with role-based instruction hierarchy.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InputMessage extends ContentMessage implements ConversationItem {

    /**
     * Message content. Can be string or array of input content items.
     */
    @JsonSerialize(using = InputContentValue.InputContentValueSerializer.class)
    @JsonDeserialize(using = InputContentValue.InputContentValueDeserializer.class)
    private InputContentValue content;

    @Override
    public String getType() {
        return super.getType();
    }
}
