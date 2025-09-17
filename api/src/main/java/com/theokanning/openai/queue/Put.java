package com.theokanning.openai.queue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

/**
 * Represents a queue put operation for task queuing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Put {
    /**
     * The endpoint URL for the queue operation
     */
    private String endpoint;

    /**
     * The name of the queue
     */
    private String queue;

    /**
     * Used for fine-grained priority control in callback mode Lower numbers have higher priority (1 > 2 > 3) Currently not supported
     */
    private Integer level;

    /**
     * The data payload to be processed
     */
    private Map<String, Object> data;

    /**
     * The response mode for the operation (default: "callback") Supported modes: "callback", "blocking", "streaming"
     */
    @Builder.Default
    private String responseMode = "callback";

    /**
     * The callback URL for asynchronous responses
     */
    @JsonProperty("callback_url")
    private String callbackUrl;

    /**
     * The timeout duration for the operation in seconds
     */
    private int timeout;

    /**
     * Additional metadata for the task
     */
    private String metadata;

    /**
     * Returns the full queue name by combining the queue name and level
     *
     * @return formatted queue name as "queueName:level"
     */
    @JsonIgnore
    public String getFullQueueName() {
        return String.format("%s:%d", queue, level);
    }

    /**
     * Determines the effective queue level based on the response mode and level
     *
     * @return the effective queue level
     */
    @JsonIgnore
    public Integer getQueueLevel() {
        if("blocking".equals(responseMode) || "streaming".equals(responseMode)) {
            return 0;
        } else if("callback".equals(responseMode) || "batch".equals(responseMode)) {
            return getLevel() != null && getLevel() >= 0 ? getLevel() : 1;
        } else {
            throw new IllegalArgumentException("Unsupported response mode: " + responseMode);
        }
    }

    /**
     * Determines the task timeout based on the response mode and specified timeout
     *
     * @return the task timeout in seconds
     */
    @JsonIgnore
    public int getTaskTimeout() {
        if("blocking".equals(responseMode) || "streaming".equals(responseMode)) {
            return timeout > 0 ? Math.min(timeout, 300) : 300;
        } else if("callback".equals(responseMode) || "batch".equals(responseMode)) {
            return timeout > 0 ? timeout : 24 * 60 * 60;
        } else {
            throw new IllegalArgumentException("Unsupported response mode: " + responseMode);
        }
    }
}
