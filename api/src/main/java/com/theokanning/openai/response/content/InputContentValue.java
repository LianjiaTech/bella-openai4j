package com.theokanning.openai.response.content;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.List;

/**
 * Wrapper for input message content that can be either a string or array of input content items.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@NoArgsConstructor
public class InputContentValue {

    private String stringValue;
    private List<InputContent> arrayValue;
    private boolean isString;

    public InputContentValue(String value) {
        this.stringValue = value;
        this.isString = true;
    }

    public InputContentValue(List<InputContent> value) {
        this.arrayValue = value;
        this.isString = false;
    }

    public static InputContentValue of(String text) {
        return new InputContentValue(text);
    }

    public static InputContentValue of(List<InputContent> content) {
        return new InputContentValue(content);
    }

    public static class InputContentValueSerializer extends JsonSerializer<InputContentValue> {
        @Override
        public void serialize(InputContentValue value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if(value.isString) {
                gen.writeString(value.stringValue);
            } else {
                gen.writeObject(value.arrayValue);
            }
        }
    }

    public static class InputContentValueDeserializer extends JsonDeserializer<InputContentValue> {
        @Override
        public InputContentValue deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if(parser.getCurrentToken() == JsonToken.VALUE_STRING) {
                return InputContentValue.of(parser.getText());
            } else if(parser.getCurrentToken() == JsonToken.START_ARRAY) {
                ObjectMapper mapper = (ObjectMapper) parser.getCodec();
                List<InputContent> array = mapper.readValue(parser, new TypeReference<List<InputContent>>() {
                });
                return InputContentValue.of(array);
            } else {
                throw context.wrongTokenException(parser, InputContentValue.class, JsonToken.VALUE_STRING,
                        "Expected string or array for input message content");
            }
        }
    }
}
