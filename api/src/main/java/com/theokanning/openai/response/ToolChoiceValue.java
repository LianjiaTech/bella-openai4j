package com.theokanning.openai.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.theokanning.openai.response.tool.definition.WebSearchTool;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for tool_choice field that can be a string, allowed_tools object, or specific tool object.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@NoArgsConstructor
@JsonSerialize(using = ToolChoiceValue.ToolChoiceValueSerializer.class)
@JsonDeserialize(using = ToolChoiceValue.ToolChoiceValueDeserializer.class)
public class ToolChoiceValue {

    private String stringValue;
    private Object objectValue; // Can be AllowedToolsChoice or SpecificToolChoice
    private boolean isString;

    public boolean isAllowedChoice() {
       return !isString && objectValue instanceof AllowedToolsChoice;
    }

    public boolean isSpecificChoice() {
        return !isString && objectValue instanceof SpecificToolChoice;
    }

    public ToolChoiceValue(String value) {
        this.stringValue = value;
        this.isString = true;
    }

    public ToolChoiceValue(Object value) {
        this.objectValue = value;
        this.isString = false;
    }

    public static ToolChoiceValue of(String choice) {
        return new ToolChoiceValue(choice);
    }

    public static ToolChoiceValue of(AllowedToolsChoice choice) {
        return new ToolChoiceValue(choice);
    }
    
    public static ToolChoiceValue of(SpecificToolChoice choice) {
        return new ToolChoiceValue(choice);
    }
    
    // Factory methods for allowed tools
    public static ToolChoiceValue allowedTools(String mode, List<ToolDefinition> tools) {
        return new ToolChoiceValue(new AllowedToolsChoice(mode, tools));
    }
    
    // Factory methods for specific tools
    public static ToolChoiceValue function(String functionName) {
        return new ToolChoiceValue(SpecificToolChoice.function(functionName));
    }
    
    public static ToolChoiceValue mcp(String serverLabel, String toolName) {
        return new ToolChoiceValue(SpecificToolChoice.mcp(serverLabel, toolName));
    }
    
    public static ToolChoiceValue mcp(String serverLabel) {
        return new ToolChoiceValue(SpecificToolChoice.mcp(serverLabel));
    }
    
    public static ToolChoiceValue hosted(String toolType) {
        return new ToolChoiceValue(SpecificToolChoice.hosted(toolType));
    }
    
    public static ToolChoiceValue custom(String toolName) {
        return new ToolChoiceValue(SpecificToolChoice.custom(toolName));
    }
    
    // Convenience constants for hosted tools
    public static final ToolChoiceValue FILE_SEARCH = hosted("file_search");
    public static final ToolChoiceValue WEB_SEARCH = hosted("web_search_preview");
    public static final ToolChoiceValue COMPUTER_USE = hosted("computer_use_preview");
    public static final ToolChoiceValue CODE_INTERPRETER = hosted("code_interpreter");
    public static final ToolChoiceValue IMAGE_GENERATION = hosted("image_generation");

    /**
     * Represents an allowed_tools choice that constrains tools to a predefined set.
     */
    @Data
    @NoArgsConstructor
    public static class AllowedToolsChoice {
        private final String type = "allowed_tools";
        private String mode; // "auto" or "required"
        private List<ToolDefinition> tools;
        
        public AllowedToolsChoice(String mode, List<ToolDefinition> tools) {
            this.mode = mode;
            this.tools = tools;
        }
    }
    
    /**
     * Represents a specific tool choice that forces a particular tool to be called.
     */
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SpecificToolChoice {
        private String type;
        
        // For function and custom tools - the function/tool name
        private String name;
        
        // For MCP tools only
        @JsonProperty("server_label") 
        private String serverLabel;
        
        /**
         * Creates a function tool choice: {"type": "function", "name": "function_name"}
         */
        public static SpecificToolChoice function(String functionName) {
            SpecificToolChoice choice = new SpecificToolChoice();
            choice.type = "function";
            choice.name = functionName;
            return choice;
        }
        
        /**
         * Creates an MCP tool choice: {"type": "mcp", "server_label": "label", "name": "tool_name"}
         */
        public static SpecificToolChoice mcp(String serverLabel, String toolName) {
            SpecificToolChoice choice = new SpecificToolChoice();
            choice.type = "mcp";
            choice.serverLabel = serverLabel;
            choice.name = toolName; // Optional, can be null
            return choice;
        }
        
        /**
         * Creates an MCP tool choice without specific tool name: {"type": "mcp", "server_label": "label"}
         */
        public static SpecificToolChoice mcp(String serverLabel) {
            SpecificToolChoice choice = new SpecificToolChoice();
            choice.type = "mcp";
            choice.serverLabel = serverLabel;
            return choice;
        }
        
        /**
         * Creates a hosted tool choice: {"type": "file_search"} (no additional fields)
         */
        public static SpecificToolChoice hosted(String toolType) {
            SpecificToolChoice choice = new SpecificToolChoice();
            choice.type = toolType; // e.g., "file_search", "web_search_preview", "code_interpreter"
            return choice;
        }
        
        /**
         * Creates a custom tool choice: {"type": "custom", "name": "tool_name"}
         */
        public static SpecificToolChoice custom(String toolName) {
            SpecificToolChoice choice = new SpecificToolChoice();
            choice.type = "custom";
            choice.name = toolName;
            return choice;
        }
    }
    
    /**
     * Simple tool definition for use in allowed_tools.
     */
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ToolDefinition {
        private String type;
        private String name; // For function, custom tools
        @JsonProperty("server_label")
        private String serverLabel; // For MCP tools
        
        public static ToolDefinition function(String name) {
            ToolDefinition def = new ToolDefinition();
            def.type = "function";
            def.name = name;
            return def;
        }
        
        public static ToolDefinition mcp(String serverLabel) {
            ToolDefinition def = new ToolDefinition();
            def.type = "mcp";
            def.serverLabel = serverLabel;
            return def;
        }
        
        public static ToolDefinition hosted(String type) {
            ToolDefinition def = new ToolDefinition();
            def.type = type;
            return def;
        }

        public ToolDefinition(String type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    public static class ToolChoiceValueSerializer extends JsonSerializer<ToolChoiceValue> {
        @Override
        public void serialize(ToolChoiceValue value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if(value.isString) {
                gen.writeString(value.stringValue);
            } else {
                gen.writeObject(value.objectValue);
            }
        }
    }

    public static class ToolChoiceValueDeserializer extends JsonDeserializer<ToolChoiceValue> {
        @Override
        public ToolChoiceValue deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if(parser.getCurrentToken() == JsonToken.VALUE_STRING) {
                return ToolChoiceValue.of(parser.getText());
            } else if(parser.getCurrentToken() == JsonToken.START_OBJECT) {
                ObjectMapper mapper = (ObjectMapper) parser.getCodec();
                JsonNode node = mapper.readTree(parser);
                
                // Check the type field to determine which class to deserialize into
                JsonNode typeNode = node.get("type");
                String typeValue = typeNode != null ? typeNode.asText() : null;
                
                if ("allowed_tools".equals(typeValue)) {
                    AllowedToolsChoice allowedTools = mapper.treeToValue(node, AllowedToolsChoice.class);
                    return ToolChoiceValue.of(allowedTools);
                } else {
                    SpecificToolChoice specificTool = mapper.treeToValue(node, SpecificToolChoice.class);
                    return ToolChoiceValue.of(specificTool);
                }
            } else {
                throw context.wrongTokenException(parser, ToolChoiceValue.class, JsonToken.VALUE_STRING,
                        "Expected string or object for tool_choice");
            }
        }
    }


    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Test string values
        ToolChoiceValue auto = ToolChoiceValue.of("auto");
        System.out.println("Auto: " + mapper.writeValueAsString(auto));

        ToolChoiceValue none = ToolChoiceValue.of("none");
        System.out.println("None: " + mapper.writeValueAsString(none));

        ToolChoiceValue required = ToolChoiceValue.of("required");
        System.out.println("Required: " + mapper.writeValueAsString(required));

        List<ToolDefinition> toolDefinitions = new ArrayList<>();
        toolDefinitions.add(ToolDefinition.hosted("file_search"));
        toolDefinitions.add(ToolDefinition.function("get_weather"));
        ToolChoiceValue allowed = ToolChoiceValue.allowedTools("auto", toolDefinitions);
        System.out.println("Allowed: " + mapper.writeValueAsString(allowed));

        // Test function tool choice
        ToolChoiceValue function = ToolChoiceValue.function("get_weather");
        System.out.println("Function: " + mapper.writeValueAsString(function));

        // Test MCP tool choice
        ToolChoiceValue mcp = ToolChoiceValue.mcp("deepwiki", "ask_question");
        System.out.println("MCP: " + mapper.writeValueAsString(mcp));

        // Test hosted tools
        System.out.println("File Search: " + mapper.writeValueAsString(ToolChoiceValue.FILE_SEARCH));
        System.out.println("Web Search: " + mapper.writeValueAsString(ToolChoiceValue.WEB_SEARCH));
        System.out.println("Code Interpreter: " + mapper.writeValueAsString(ToolChoiceValue.CODE_INTERPRETER));

        // Test custom tool choice
        ToolChoiceValue custom = ToolChoiceValue.custom("my_custom_tool");
        System.out.println("Custom: " + mapper.writeValueAsString(custom));

        // Test deserialization
        String jsonFunction = "{\"type\": \"function\", \"name\": \"get_weather\"}";
        ToolChoiceValue deserialized = mapper.readValue(jsonFunction, ToolChoiceValue.class);
        System.out.println("Deserialized function: " + mapper.writeValueAsString(deserialized));

        String jsonMcp = "{\"type\": \"mcp\", \"server_label\": \"deepwiki\", \"name\": \"ask_question\"}";
        ToolChoiceValue deserializedMcp = mapper.readValue(jsonMcp, ToolChoiceValue.class);
        System.out.println("Deserialized MCP: " + mapper.writeValueAsString(deserializedMcp));

        String jsonString = "\"auto\"";
        ToolChoiceValue deserializedString = mapper.readValue(jsonString, ToolChoiceValue.class);
        System.out.println("Deserialized string: " + mapper.writeValueAsString(deserializedString));
    }
}
