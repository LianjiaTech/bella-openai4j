package com.theokanning.openai.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a queue take operation to retrieve tasks from one or more queues
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Take {
    /**
     * The endpoint URL for the take operation
     */
    private String endpoint;

    /**
     * List of queue names to take tasks from
     */
    private List<String> queues;

    /**
     * Maximum number of tasks to retrieve
     */
    private Integer size;
    /**
     * worker process timeout for a task
     */
    @JsonProperty("process_timeout")
    private int processTimeout;
    /**
     * worker process max retries for a task
     */
    @JsonProperty("process_max_retries")
    private int processMaxRetries;
    /**
     * The strategy for taking tasks from queues (default: "fifo") Supported strategies: "fifo", "round_robin", "active_passive"
     */
    @Builder.Default
    private String strategy = "fifo";

    /**
     * Only retrieve tasks created within the last N seconds (0 means no time restriction)
     */
    @JsonProperty("min_age_seconds")
    private long minAgeSeconds;
}
