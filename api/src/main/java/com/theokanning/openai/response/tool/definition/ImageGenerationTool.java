package com.theokanning.openai.response.tool.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.assistants.assistant.Tool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Image generation tool definition for AI-powered image creation.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageGenerationTool implements ToolDefinition {

    /**
     * Tool type, always "image_generation".
     */
    private String type = "image_generation";

    /**
     * Model to use for generation (default: "gpt-image-1").
     */
    private String model;

    /**
     * Image size.
     */
    private String size; // "1024x1024", "1024x1536", "1536x1024", "auto"

    /**
     * Image quality.
     */
    private String quality; // "low", "medium", "high", "auto"

    /**
     * Background type.
     */
    private String background; // "transparent", "opaque", "auto"

    /**
     * Output format.
     */
    @JsonProperty("output_format")
    private String outputFormat; // "png", "webp", "jpeg"

    /**
     * Output compression level (0-100).
     */
    @JsonProperty("output_compression")
    private Integer outputCompression;

    /**
     * Number of partial images for streaming (0-3).
     */
    @JsonProperty("partial_images")
    private Integer partialImages;

    /**
     * Input fidelity level.
     */
    @JsonProperty("input_fidelity")
    private String inputFidelity; // "high", "low"

    /**
     * Content moderation setting.
     */
    private String moderation;

    /**
     * Input image mask for editing.
     */
    @JsonProperty("input_image_mask")
    private InputImageMask inputImageMask;

    @Override
    public Tool getRealTool() {
        Tool.ImgGenerate imgGenerate = new Tool.ImgGenerate();
        imgGenerate.setDefinition(this);
        return imgGenerate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputImageMask {

        /**
         * File ID for mask image.
         */
        @JsonProperty("file_id")
        private String fileId;

        /**
         * URL for mask image.
         */
        @JsonProperty("image_url")
        private String imageUrl;
    }
}
