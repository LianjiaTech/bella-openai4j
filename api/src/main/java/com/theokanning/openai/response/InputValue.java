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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.List;

/**
 * Wrapper for input field that can be: - String (text input) - List<ConversationItem> (array of mixed input objects: messages, conversation items,
 * references)
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@NoArgsConstructor
@JsonSerialize(using = InputValue.InputValueSerializer.class)
@JsonDeserialize(using = InputValue.InputValueDeserializer.class)
public class InputValue {

    private String stringValue;
    private List<ConversationItem> objectListValue;
    private InputType inputType;

    public InputValue(String value) {
        this.stringValue = value;
        this.inputType = InputType.STRING;
    }

    public static InputValue of(String text) {
        return new InputValue(text);
    }

    public static InputValue of(List<ConversationItem> objects) {
        InputValue value = new InputValue();
        value.objectListValue = objects;
        value.inputType = InputType.OBJECT_LIST;
        return value;
    }

    public enum InputType {
        STRING, OBJECT_LIST
    }

    public static class InputValueSerializer extends JsonSerializer<InputValue> {
        @Override
        public void serialize(InputValue value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            switch (value.inputType) {
            case STRING:
                gen.writeString(value.stringValue);
                break;
            case OBJECT_LIST:
                gen.writeObject(value.objectListValue);
                break;
            default:
                throw new IllegalStateException("Unknown input type: " + value.inputType);
            }
        }
    }

    public static class InputValueDeserializer extends JsonDeserializer<InputValue> {
        @Override
        public InputValue deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if(parser.getCurrentToken() == JsonToken.VALUE_STRING) {
                return InputValue.of(parser.getText());
            } else if(parser.getCurrentToken() == JsonToken.START_ARRAY) {
                ObjectMapper mapper = (ObjectMapper) parser.getCodec();
                // Directly deserialize as List<ConversationItem> - Jackson will handle polymorphic deserialization
                List<ConversationItem> objects = mapper.readValue(parser, new TypeReference<List<ConversationItem>>() {
                });
                return InputValue.of(objects);
            } else {
                throw context.wrongTokenException(parser, InputValue.class, JsonToken.VALUE_STRING,
                        "Expected string or array for input");
            }
        }
    }
}
