package com.theokanning.openai.response.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents audio input for speech processing.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InputAudio extends InputContent {

    /**
     * Content type, always "input_audio".
     */
    @Builder.Default
    private String type = "input_audio";

    /**
     * Audio input configuration.
     */
    @JsonProperty("input_audio")
    private AudioData inputAudio;

    @Override
    public String getType() {
        return type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AudioData {

        /**
         * Base64-encoded audio data.
         */
        private String data;

        /**
         * Audio format: "mp3" or "wav".
         */
        private String format;
    }
}
