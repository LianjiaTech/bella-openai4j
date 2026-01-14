package com.theokanning.openai.queue;

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
    private int processTimeout;
    /**
     * The strategy for taking tasks from queues (default: "fifo") Supported strategies: "fifo", "round_robin", "active_passive"
     */
    @Builder.Default
    private String strategy = "fifo";
}
