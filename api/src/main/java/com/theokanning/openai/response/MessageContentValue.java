package com.theokanning.openai.response;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.theokanning.openai.response.content.InputContent;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.List;

/**
 * Wrapper for message content that can be either a string or array of input content.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@NoArgsConstructor
public class MessageContentValue {

    private String stringValue;
    private List<InputContent> arrayValue;
    private boolean isString;

    public MessageContentValue(String value) {
        this.stringValue = value;
        this.isString = true;
    }

    public MessageContentValue(List<InputContent> value) {
        this.arrayValue = value;
        this.isString = false;
    }

    public static MessageContentValue of(String text) {
        return new MessageContentValue(text);
    }

    public static MessageContentValue of(List<InputContent> content) {
        return new MessageContentValue(content);
    }

    public static class MessageContentValueSerializer extends JsonSerializer<MessageContentValue> {
        @Override
        public void serialize(MessageContentValue value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if(value.isString) {
                gen.writeString(value.stringValue);
            } else {
                gen.writeObject(value.arrayValue);
            }
        }
    }

    public static class MessageContentValueDeserializer extends JsonDeserializer<MessageContentValue> {
        @Override
        public MessageContentValue deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if(parser.getCurrentToken() == JsonToken.VALUE_STRING) {
                return MessageContentValue.of(parser.getText());
            } else if(parser.getCurrentToken() == JsonToken.START_ARRAY) {
                ObjectMapper mapper = (ObjectMapper) parser.getCodec();
                List<InputContent> array = mapper.readValue(parser, new TypeReference<List<InputContent>>() {
                });
                return MessageContentValue.of(array);
            } else {
                throw context.wrongTokenException(parser, MessageContentValue.class, JsonToken.VALUE_STRING,
                        "Expected string or array for message content");
            }
        }
    }
}
