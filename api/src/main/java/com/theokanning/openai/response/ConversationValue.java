package com.theokanning.openai.response;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * Wrapper for conversation field that can be either a string ID or ConversationReference object.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@NoArgsConstructor
@JsonSerialize(using = ConversationValue.ConversationValueSerializer.class)
@JsonDeserialize(using = ConversationValue.ConversationValueDeserializer.class)
public class ConversationValue {

    private String stringValue;
    private ConversationReference objectValue;
    private boolean isString;

    public ConversationValue(String value) {
        this.stringValue = value;
        this.isString = true;
    }

    public ConversationValue(ConversationReference value) {
        this.objectValue = value;
        this.isString = false;
    }

    public static ConversationValue of(String id) {
        return new ConversationValue(id);
    }

    public static ConversationValue of(ConversationReference conversation) {
        return new ConversationValue(conversation);
    }

    public static class ConversationValueSerializer extends JsonSerializer<ConversationValue> {
        @Override
        public void serialize(ConversationValue value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if(value.isString) {
                gen.writeString(value.stringValue);
            } else {
                gen.writeObject(value.objectValue);
            }
        }
    }

    public static class ConversationValueDeserializer extends JsonDeserializer<ConversationValue> {
        @Override
        public ConversationValue deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if(parser.getCurrentToken() == JsonToken.VALUE_STRING) {
                return ConversationValue.of(parser.getText());
            } else if(parser.getCurrentToken() == JsonToken.START_OBJECT) {
                ObjectMapper mapper = (ObjectMapper) parser.getCodec();
                ConversationReference ref = mapper.readValue(parser, ConversationReference.class);
                return ConversationValue.of(ref);
            } else {
                throw context.wrongTokenException(parser, ConversationValue.class, JsonToken.VALUE_STRING,
                        "Expected string or object for conversation");
            }
        }
    }
}
