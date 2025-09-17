package com.theokanning.openai.queue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represents a task in the queue system with execution details and status information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    /**
     * Unique identifier for the task
     */
    @JsonProperty("task_id")
    private String taskId;

    /**
     * Custom identifier provided by the user
     */
    @JsonProperty("custom_id")
    private String customId;

    /**
     * Identifier for the batch this task belongs to
     */
    @JsonProperty("batch_id")
    private String batchId;

    /**
     * Access key for authentication
     */
    private String ak;

    /**
     * The endpoint where the task will be executed
     */
    private String endpoint;

    /**
     * The name of the queue containing this task
     */
    private String queue;

    /**
     * The priority level of the task in the queue
     */
    @Builder.Default
    private Integer level = 0;

    /**
     * The data payload for the task
     */
    private Map<String, Object> data;

    /**
     * Current status of the task (e.g., "pending", "running", "succeeded", "failed", "cancelled")
     */
    private String status;

    /**
     * The result of the task execution
     */
    private Object result;

    /**
     * Identifier of the instance executing this task
     */
    @JsonProperty("instance_id")
    private String instanceId;

    /**
     * Timestamp when the task started execution
     */
    @JsonProperty("start_time")
    private long startTime;

    /**
     * Duration the task has been running in milliseconds
     */
    @JsonProperty("running_time")
    private long runningTime;

    /**
     * Timestamp when the task is set to expire
     */
    @JsonProperty("expire_time")
    private long expireTime;

    /**
     * Timestamp when the task completed execution
     */
    @JsonProperty("completed_time")
    private long completedTime;

    /**
     * URL to call back with task results
     */
    @JsonProperty("callback_url")
    private String callbackUrl;

    /**
     * Mode for delivering task responses
     */
    @JsonProperty("response_mode")
    private String responseMode;

    /**
     * additional metadata for the task
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
     * Checks if the task has finished execution (succeeded, failed, or cancelled)
     *
     * @return true if the task is in a finished state
     */
    @JsonIgnore
    public boolean isFinish() {
        return "succeeded".equals(status) || "failed".equals(status) || "cancelled".equals(status) || "timeout".equals(status);
    }

    /**
     * Checks if the task has expired based on the current time and its expireTime
     *
     * @return true if the task is expired
     */
    @JsonIgnore
    public boolean isExpire() {
        return System.currentTimeMillis() > expireTime;
    }
}
