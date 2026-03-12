package com.theokanning.openai.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the basic information of a queue, including the service endpoint and queue name. Used to identify a task queue under a specific service
 * endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Queue {
    /**
     * The service endpoint URL this queue belongs to
     */
    String endpoint;

    /**
     * The name of the queue
     */
    String queue;
}
