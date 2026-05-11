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
 * Wrapper for output message content that can be either a string or array of output content items.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@NoArgsConstructor
public class OutputContentValue {

    private String stringValue;
    private List<OutputContent> arrayValue;
    private boolean isString;

    public OutputContentValue(String value) {
        this.stringValue = value;
        this.isString = true;
    }

    public OutputContentValue(List<OutputContent> value) {
        this.arrayValue = value;
        this.isString = false;
    }

    public static OutputContentValue of(String text) {
        return new OutputContentValue(text);
    }

    public static OutputContentValue of(List<OutputContent> content) {
        return new OutputContentValue(content);
    }

    public static class OutputContentValueSerializer extends JsonSerializer<OutputContentValue> {
        @Override
        public void serialize(OutputContentValue value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if(value.isString) {
                gen.writeString(value.stringValue);
            } else {
                gen.writeObject(value.arrayValue);
            }
        }
    }

    public static class OutputContentValueDeserializer extends JsonDeserializer<OutputContentValue> {
        @Override
        public OutputContentValue deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if(parser.getCurrentToken() == JsonToken.VALUE_STRING) {
                return OutputContentValue.of(parser.getText());
            } else if(parser.getCurrentToken() == JsonToken.START_ARRAY) {
                ObjectMapper mapper = (ObjectMapper) parser.getCodec();
                List<OutputContent> array = mapper.readValue(parser, new TypeReference<List<OutputContent>>() {
                });
                return OutputContentValue.of(array);
            } else {
                throw context.wrongTokenException(parser, OutputContentValue.class, JsonToken.VALUE_STRING,
                        "Expected string or array for output message content");
            }
        }
    }
}
