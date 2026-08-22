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
 * A request for OpenAi to edit an image based on a prompt
 * All fields except prompt are optional
 *
 * https://beta.openai.com/docs/api-reference/images/create-edit
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateImageEditRequest {

    /**
     * A text description of the desired image(s). The maximum length in 1000 characters.
     */
    @NonNull
    String prompt;

    /**
     * The model to use for image generation. Only dall-e-2 is supported at this time. Defaults to dall-e-2.
     */
    String model;

    /**
     * The number of images to generate. Must be between 1 and 10. Defaults to 1.
     */
    Integer n;

    /**
     * The size of the generated images. Must be one of "256x256", "512x512", or "1024x1024". Defaults to "1024x1024".
     */
    String size;

    /**
     * The format in which the generated images are returned. Must be one of url or b64_json. Defaults to url.
     */
    @JsonProperty("response_format")
    String responseFormat;

    /**
     * A unique identifier representing your end-user, which will help OpenAI to monitor and detect abuse.
     */
    String user;

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
