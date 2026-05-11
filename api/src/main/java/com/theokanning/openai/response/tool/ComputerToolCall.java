package com.theokanning.openai.response.tool;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.theokanning.openai.response.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A tool call to a computer use tool.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComputerToolCall extends ToolCall {

    /**
     * The type of the computer call. Always "computer_call".
     */
    @Builder.Default
    private String type = "computer_call";

    /**
     * The unique ID of the computer call.
     */
    private String id;

    /**
     * An identifier used when responding to the tool call with output.
     */
    @JsonProperty("call_id")
    private String callId;

    /**
     * Computer action to perform.
     */
    private ComputerAction action;

    /**
     * The pending safety checks for the computer call.
     */
    @JsonProperty("pending_safety_checks")
    private List<String> pendingSafetyChecks;

    /**
     * The status of the item.
     */
    private ItemStatus status;

    @Override
    public String getType() {
        return type;
    }

    /**
     * Base class for computer actions.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ComputerToolCall.ClickAction.class, name = "click"),
            @JsonSubTypes.Type(value = ComputerToolCall.DoubleClickAction.class, name = "double_click"),
            @JsonSubTypes.Type(value = ComputerToolCall.DragAction.class, name = "drag"),
            @JsonSubTypes.Type(value = ComputerToolCall.KeyPressAction.class, name = "keypress"),
            @JsonSubTypes.Type(value = ComputerToolCall.MoveAction.class, name = "move"),
            @JsonSubTypes.Type(value = ComputerToolCall.ScreenshotAction.class, name = "screenshot"),
            @JsonSubTypes.Type(value = ComputerToolCall.ScrollAction.class, name = "scroll"),
            @JsonSubTypes.Type(value = ComputerToolCall.TypeAction.class, name = "type"),
            @JsonSubTypes.Type(value = ComputerToolCall.WaitAction.class, name = "wait")
    })
    public static abstract class ComputerAction {
        public abstract String getType();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClickAction extends ComputerAction {
        private String type = "click";
        private Integer x;
        private Integer y;
        private String button; // left, right, wheel, back, forward

        @Override
        public String getType() {
            return type;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoubleClickAction extends ComputerAction {
        private String type = "double_click";
        private Integer x;
        private Integer y;

        @Override
        public String getType() {
            return type;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DragAction extends ComputerAction {
        private String type = "drag";
        private List<Coordinate> path;

        @Override
        public String getType() {
            return type;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Coordinate {
            private Integer x;
            private Integer y;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyPressAction extends ComputerAction {
        private String type = "keypress";
        private List<String> keys;

        @Override
        public String getType() {
            return type;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoveAction extends ComputerAction {
        private String type = "move";
        private Integer x;
        private Integer y;

        @Override
        public String getType() {
            return type;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScreenshotAction extends ComputerAction {
        private String type = "screenshot";

        @Override
        public String getType() {
            return type;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScrollAction extends ComputerAction {
        private String type = "scroll";
        private Integer x;
        private Integer y;
        @JsonProperty("scroll_x")
        private Integer scrollX;
        @JsonProperty("scroll_y")
        private Integer scrollY;

        @Override
        public String getType() {
            return type;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeAction extends ComputerAction {
        private String type = "type";
        private String text;

        @Override
        public String getType() {
            return type;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaitAction extends ComputerAction {
        private String type = "wait";

        @Override
        public String getType() {
            return type;
        }
    }
}
