package com.theokanning.openai.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metrics {
    /**
     * between read head and write head
     */
    private int delayed;
}
