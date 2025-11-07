package com.theokanning.openai.assistants.run;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import lombok.Data;

import java.io.IOException;
import java.util.List;

/**
 * https://platform.openai.com/docs/guides/function-calling/function-calling-behavior
 * @author LiangTao
 * @date 2024年04月18 17:18
 **/
@Data
@JsonSerialize(using = ToolChoice.Serializer.class)
@JsonDeserialize(using = ToolChoice.Deserializer.class)
public class ToolChoice {
    public static final ToolChoice REQUIRED = new ToolChoice("required");

    public static final ToolChoice NONE = new ToolChoice("none");

    public static final ToolChoice AUTO = new ToolChoice("auto");

    /**
     * The name of the function to call.
     */
    Function function;

    /**
     * which tolls is allowed
     */
    AllowedTools allowedTools;

    /**
     * The type of the tool. If type is function, the function name must be set
     * enum: none/auto/function/required
     */
    String type;


    private ToolChoice(String type) {
        this.type = type;
    }

    public ToolChoice(Function function) {
        this.type = "function";
        if (function == null) {
            throw new IllegalArgumentException("Function must not be null");
        }
        this.function = function;
    }

    public ToolChoice(AllowedTools allowedTools) {
        this.type = "allowed_tools";
        if (allowedTools == null || allowedTools.getTools().isEmpty()) {
            throw new IllegalArgumentException("allowedTools must not be empty");
        }
        this.allowedTools = allowedTools;
    }

    public static class Deserializer extends JsonDeserializer<ToolChoice> {

        @Override
        public ToolChoice deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            if (jsonParser.getCurrentToken() == JsonToken.VALUE_STRING) {
                String type = jsonParser.getText();
                switch (type) {
                    case "none":
                        return ToolChoice.NONE;
                    case "auto":
                        return ToolChoice.AUTO;
                    case "required":
                        return ToolChoice.REQUIRED;
                    default:
                        return new ToolChoice(type);
                }
            }
            if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                // 处理对象的情况
                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    String fieldName = jsonParser.getCurrentName();
                    if ("function".equals(fieldName)) {
                        jsonParser.nextToken();
                        ToolChoice toolChoice = new ToolChoice("function");
                        toolChoice.function = parseFunction(jsonParser);
                        return toolChoice;
                    } else if ("allowed_tools".equals(fieldName)) {
                        jsonParser.nextToken();
                        ToolChoice toolChoice = new ToolChoice("allowed_tools");
                        toolChoice.allowedTools = parseAllowedTools(jsonParser);
                        return toolChoice;
                    }
                }
            }
            //抛出异常
            throw new IllegalArgumentException("Invalid ToolChoice");
        }

        private Function parseFunction(JsonParser jsonParser) throws IOException {
            JsonToken currentToken = jsonParser.getCurrentToken();

            if (currentToken == JsonToken.VALUE_NULL) {
                return null;
            }

            if (currentToken != JsonToken.START_OBJECT) {
                throw new IOException("Expected START_OBJECT for Function, but got: " + currentToken);
            }

            // 处理对象的情况
            Function function = new Function();
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                // 判断对象内元素类型并进行相应的反序列化
                if (jsonParser.getCurrentName().equals("name")) {
                    function.setName(jsonParser.nextTextValue());
                }
            }
            return function;
        }

        private AllowedTools parseAllowedTools(JsonParser jsonParser) throws IOException {
            JsonToken currentToken = jsonParser.getCurrentToken();

            if (currentToken == JsonToken.VALUE_NULL) {
                return null;
            }

            if (currentToken != JsonToken.START_OBJECT) {
                throw new IOException("Expected START_OBJECT for AllowedTools, but got: " + currentToken);
            }

            AllowedTools allowedTools = new AllowedTools();
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jsonParser.getCurrentName();
                if ("mode".equals(fieldName)) {
                    allowedTools.setMode(jsonParser.nextTextValue());
                } else if ("tools".equals(fieldName)) {
                    jsonParser.nextToken();
                    allowedTools.setTools(parseToolsList(jsonParser));
                }
            }
            return allowedTools;
        }

        private List<Tool> parseToolsList(JsonParser jsonParser) throws IOException {
            JsonToken currentToken = jsonParser.getCurrentToken();

            if (currentToken == JsonToken.VALUE_NULL) {
                return null;
            }

            if (currentToken != JsonToken.START_ARRAY) {
                throw new IOException("Expected START_ARRAY for Tools, but got: " + currentToken);
            }

            List<Tool> tools = new java.util.ArrayList<>();
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                tools.add(parseTool(jsonParser));
            }
            return tools;
        }

        private Tool parseTool(JsonParser jsonParser) throws IOException {
            JsonToken currentToken = jsonParser.getCurrentToken();

            if (currentToken == JsonToken.VALUE_NULL) {
                return null;
            }

            if (currentToken != JsonToken.START_OBJECT) {
                throw new IOException("Expected START_OBJECT for Tool, but got: " + currentToken);
            }

            Tool tool = new Tool();
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jsonParser.getCurrentName();
                if ("type".equals(fieldName)) {
                    tool.setType(jsonParser.nextTextValue());
                } else if ("function".equals(fieldName)) {
                    jsonParser.nextToken();
                    tool.setFunction(parseFunction(jsonParser));
                }
            }
            return tool;
        }
    }

    public static class Serializer extends JsonSerializer<ToolChoice> {
        @Override
        public void serialize(ToolChoice toolChoice, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            String type = toolChoice.getType();
            switch (type) {
                case "none":
                case "auto":
                case "required":
                    jsonGenerator.writeString(type);
                    break;
                case "function":
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("type", type);
                    jsonGenerator.writeObjectField("function", toolChoice.getFunction());
                    jsonGenerator.writeEndObject();
                    break;
                case "allowed_tools":
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("type", type);
                    jsonGenerator.writeObjectField("allowed_tools", toolChoice.getAllowedTools());
                    jsonGenerator.writeEndObject();
                    break;
                default:
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("type", type);
                    jsonGenerator.writeEndObject();
            }
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String str = "{\n"
                + "\t\"type\": \"allowed_tools\",\n"
                + "\t\"allowed_tools\": {\n"
                + "\t\t\"mode\": \"auto\",\n"
                + "\t\t\"tools\": [{\n"
                + "\t\t\t\"type\": \"function\",\n"
                + "\t\t\t\"function\": {\n"
                + "\t\t\t\t\"name\": \"get_weather\"\n"
                + "\t\t\t}\n"
                + "\t\t}, {\n"
                + "\t\t\t\"type\": \"function\",\n"
                + "\t\t\t\"function\": {\n"
                + "\t\t\t\t\"name\": \"get_time\"\n"
                + "\t\t\t}\n"
                + "\t\t}]\n"
                + "\t}\n"
                + "}";
        ToolChoice toolChoice = mapper.readValue(str, ToolChoice.class);
        System.out.println(mapper.writeValueAsString(toolChoice));
        String str2 = "{\n"
                + "\t\"type\": \"function\",\n"
                + "\t\"function\": {\n"
                + "\t\t\"name\": \"get_weather\"\n"
                + "\t}\n"
                + "}";
        ToolChoice toolChoice2 = mapper.readValue(str2, ToolChoice.class);
        System.out.println(mapper.writeValueAsString(toolChoice2));

        String str3 = "{\n"
                + "\t\"tool_choice\": \"auto\"\n"
                + "}";
        ChatCompletionRequest request = mapper.readValue(str3, ChatCompletionRequest.class);
        System.out.println(mapper.writeValueAsString(request));
    }

}
