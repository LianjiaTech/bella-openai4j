package com.theokanning.openai.image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * A request for OpenAi to create an image based on a prompt
 * All fields except prompt are optional
 *
 * https://beta.openai.com/docs/api-reference/images/create
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateImageRequest {

    /**
     * A text description of the desired image(s). The maximum length is 1000 characters for dall-e-2 and 4000 characters for dall-e-3.
     */
    @NonNull
    String prompt;

    /**
     * The model to use for image generation. Defaults to "dall-e-2".
     */
    String model;

    /**
     * The number of images to generate. Must be between 1 and 10. For dall-e-3, only n=1 is supported. Defaults to 1.
     */
    Integer n;

    /**
     * The quality of the image that will be generated. "hd" creates images with finer details and greater consistency across the image. This param is only supported for dall-e-3. Defaults to "standard".
     */
    String quality;

    /**
     * The size of the generated images. Must be one of 256x256, 512x512, or 1024x1024 for dall-e-2. Must be one of 1024x1024, 1792x1024, or 1024x1792 for dall-e-3 models. Defaults to 1024x1024.
     */
    String size;

    /**
     * The format in which the generated images are returned. Must be one of url or b64_json. Defaults to url.
     */
    @JsonProperty("response_format")
    String responseFormat;

    /**
     * The style of the generated images. Must be one of vivid or natural. Vivid causes the model to lean towards generating hyper-real and dramatic images. Natural causes the model to produce more natural, less hyper-real looking images. This param is only supported for dall-e-3. Defaults to vivid.
     */
    String style;

    /**
     * A unique identifier representing your end-user, which will help OpenAI to monitor and detect abuse.
     */
    String user;

    /**
     * doubao模型使用：是否生成水印
     */
    Boolean watermark;

    /**
     * The format in which the generated images are returned. This parameter is only supported for gpt-image-1. Must be one of png, jpeg, or webp.
     */
    @JsonProperty("output_format")
    String outputFormat;


    /**
     * Allows to set transparency for the background of the generated image(s). This parameter is only supported for gpt-image-1. Must be one of transparent, opaque or auto (default value). When auto is used, the model will automatically determine the best background for the image.
     * If transparent, the output format needs to support transparency, so it should be set to either png (default value) or webp.
     */
    String background;

    /**
     * Whether or not content filter should be enabled for this image request. This param is only supported for mlx-serve. Defaults to false.
     */
    boolean safety;

    /**
     * LoRA adapters to attach to the request.
     *
     * Multiple adapters are supported and are sent as lora_paths + lora_scales.
     */
    @JsonIgnore
    @Builder.Default
    List<LoraAdapter> loras = new ArrayList<>();

    /**
     * Paths for all LoRA adapters that have a path selected.
     */
    @JsonProperty("lora_paths")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getLoraPaths() {
        if (loras == null) {
            return Collections.emptyList();
        }

        return loras.stream().filter(lora -> lora != null && lora.getPath() != null && !lora.getPath().isEmpty())
                .map(LoraAdapter::getPath).collect(Collectors.toList());
    }

    /**
     * Scales corresponding to lora_paths.
     */
    @JsonProperty("lora_scales")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Double> getLoraScales() {
        if (loras == null) {
            return Collections.emptyList();
        }

        return loras.stream().filter(lora -> lora != null && lora.getPath() != null && !lora.getPath().isEmpty())
                .map(LoraAdapter::getScale).collect(Collectors.toList());
    }
}
