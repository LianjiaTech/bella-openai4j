package com.theokanning.openai.response.tool.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.response.ConversationItem;
import com.theokanning.openai.response.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * The output of a computer tool call.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComputerToolCallOutput implements ConversationItem {

    /**
     * The type of the computer tool call output. Always "computer_call_output".
     */
    @Builder.Default
    private String type = "computer_call_output";

    /**
     * The unique ID of the computer tool call output.
     */
    private String id;

    /**
     * The ID of the computer tool call that produced the output.
     */
    @JsonProperty("call_id")
    private String callId;

    /**
     * A computer screenshot image used with the computer use tool.
     */
    private ComputerScreenshot output;

    /**
     * The safety checks reported by the API that have been acknowledged by the developer.
     */
    @JsonProperty("acknowledged_safety_checks")
    private List<String> acknowledgedSafetyChecks;

    /**
     * The status of the message input.
     */
    private ItemStatus status;

    @Override
    public String getType() {
        return type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ComputerScreenshot {
        /**
         * The type of the screenshot. Always "image".
         */
        private String type = "image";

        /**
         * The base64-encoded image data.
         */
        @JsonProperty("image_url")
        private String imageUrl;

        /**
         * The format of the image.
         */
        private String format;

        /**
         * The width of the screenshot in pixels.
         */
        private Integer width;

        /**
         * The height of the screenshot in pixels.
         */
        private Integer height;
    }
}
