package com.theokanning.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.response.CreateResponseRequest;
import com.theokanning.openai.response.tool.definition.ApplyPatchTool;
import com.theokanning.openai.response.tool.definition.ComputerTool;
import com.theokanning.openai.response.tool.definition.FunctionShellTool;
import com.theokanning.openai.response.tool.definition.FunctionTool;
import com.theokanning.openai.response.tool.definition.NamespaceTool;
import com.theokanning.openai.response.tool.definition.ToolDefinition;
import com.theokanning.openai.response.tool.definition.ToolSearchTool;
import com.theokanning.openai.response.tool.definition.UnknownToolDefinition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResponseToolDefinitionTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldDeserializeCurrentOfficialToolTypes() throws Exception {
        String json = "{"
                + "\"model\":\"gpt-5\","
                + "\"tools\":["
                + "{\"type\":\"apply_patch\"},"
                + "{\"type\":\"computer\"},"
                + "{\"type\":\"shell\",\"environment\":{\"type\":\"local\"}},"
                + "{\"type\":\"tool_search\",\"description\":\"Find a deferred tool\",\"execution\":\"client\",\"parameters\":{\"type\":\"object\"}},"
                + "{\"type\":\"namespace\",\"name\":\"crm\",\"description\":\"CRM tools\",\"tools\":[{\"type\":\"function\",\"name\":\"lookup_customer\",\"parameters\":{\"type\":\"object\"}}]}"
                + "]"
                + "}";

        CreateResponseRequest request = objectMapper.readValue(json, CreateResponseRequest.class);
        List<ToolDefinition> tools = request.getTools();

        assertNotNull(tools);
        assertEquals(5, tools.size());
        assertInstanceOf(ApplyPatchTool.class, tools.get(0));
        assertInstanceOf(ComputerTool.class, tools.get(1));
        assertInstanceOf(FunctionShellTool.class, tools.get(2));
        assertInstanceOf(ToolSearchTool.class, tools.get(3));
        NamespaceTool namespaceTool = assertInstanceOf(NamespaceTool.class, tools.get(4));
        assertEquals("crm", namespaceTool.getName());
        assertEquals(1, namespaceTool.getTools().size());
        assertInstanceOf(FunctionTool.class, namespaceTool.getTools().get(0));
    }

    @Test
    void shouldFallbackForUnknownToolTypeAndPreserveProperties() throws Exception {
        String json = "{"
                + "\"type\":\"future_tool\","
                + "\"display_name\":\"Future Tool\","
                + "\"config\":{\"enabled\":true}"
                + "}";

        ToolDefinition tool = objectMapper.readValue(json, ToolDefinition.class);

        UnknownToolDefinition unknown = assertInstanceOf(UnknownToolDefinition.class, tool);
        assertEquals("future_tool", unknown.getType());
        assertEquals("Future Tool", unknown.getAdditionalProperties().get("display_name").asText());
        assertEquals(true, unknown.getAdditionalProperties().get("config").get("enabled").asBoolean());

        JsonNode serialized = objectMapper.readTree(objectMapper.writeValueAsString(unknown));
        assertEquals("future_tool", serialized.get("type").asText());
        assertEquals("Future Tool", serialized.get("display_name").asText());
        assertEquals(true, serialized.get("config").get("enabled").asBoolean());
    }
}
