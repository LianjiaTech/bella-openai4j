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
import com.theokanning.openai.response.content.InputMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.List;

/**
 * Wrapper for instructions field that can be either a string or array of InputMessage.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@NoArgsConstructor
@JsonSerialize(using = InstructionsValue.InstructionsValueSerializer.class)
@JsonDeserialize(using = InstructionsValue.InstructionsValueDeserializer.class)
public class InstructionsValue {

    private String stringValue;
    private List<InputMessage> arrayValue;
    private boolean isString;

    public InstructionsValue(String value) {
        this.stringValue = value;
        this.isString = true;
    }

    public InstructionsValue(List<InputMessage> value) {
        this.arrayValue = value;
        this.isString = false;
    }

    public static InstructionsValue of(String text) {
        return new InstructionsValue(text);
    }

    public static InstructionsValue of(List<InputMessage> messages) {
        return new InstructionsValue(messages);
    }

    public static class InstructionsValueSerializer extends JsonSerializer<InstructionsValue> {
        @Override
        public void serialize(InstructionsValue value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if(value.isString) {
                gen.writeString(value.stringValue);
            } else {
                gen.writeObject(value.arrayValue);
            }
        }
    }

    public static class InstructionsValueDeserializer extends JsonDeserializer<InstructionsValue> {
        @Override
        public InstructionsValue deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if(parser.getCurrentToken() == JsonToken.VALUE_STRING) {
                return InstructionsValue.of(parser.getText());
            } else if(parser.getCurrentToken() == JsonToken.START_ARRAY) {
                ObjectMapper mapper = (ObjectMapper) parser.getCodec();
                List<InputMessage> array = mapper.readValue(parser, new TypeReference<List<InputMessage>>() {
                });
                return InstructionsValue.of(array);
            } else {
                throw context.wrongTokenException(parser, InstructionsValue.class, JsonToken.VALUE_STRING,
                        "Expected string or array for instructions");
            }
        }
    }
}
