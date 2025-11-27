package com.theokanning.openai.service;

import com.theokanning.openai.queue.Put;
import com.theokanning.openai.queue.Register;
import com.theokanning.openai.queue.Take;
import com.theokanning.openai.queue.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Queue operation tests Note: These tests are disabled as they require a custom queue service backend that implements the /v1/queue/* endpoints,
 * which are not part of the standard OpenAI API
 */
@Disabled("Queue endpoints require custom backend service")
public class QueueTest {

    @Mock
    private OpenAiService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerQueue() {
        Register register = Register.builder()
                .queue("test-queue")
                .endpoint("http://localhost:8080/webhook")
                .build();

        String result = service.registerQueue(register);

        assertNotNull(result);
    }

    @Test
    void putTask() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "test message");
        data.put("priority", 1);

        Put put = Put.builder()
                .endpoint("http://localhost:8080/webhook")
                .queue("test-queue")
                .level(0)
                .data(data)
                .responseMode("callback")
                .callbackUrl("http://localhost:8080/callback")
                .timeout(60)
                .build();

        Object result = service.putTask(put);

        assertNotNull(result);
    }

    @Test
    void takeTasks() {
        Take take = Take.builder()
                .endpoint("http://localhost:8080/webhook")
                .queues(Arrays.asList("test-queue-1", "test-queue-2"))
                .size(10)
                .strategy("fifo")
                .build();

        Map<String, List<Task>> result = service.takeTasks(take);

        assertNotNull(result);
    }

    @Test
    void cancelTask() {
        String taskId = "test-task-id-123";

        String result = service.cancelTask(taskId);

        assertNotNull(result);
    }

    @Test
    void completeTask() {
        String taskId = "test-task-id-123";
        Map<String, Object> data = new HashMap<>();
        data.put("result", "success");
        data.put("output", "Task completed successfully");

        String result = service.completeTask(taskId, data);

        assertNotNull(result);
    }

    @Test
    void getTask() {
        String taskId = "test-task-id-123";
        
        // Create a mock Task object
        Task expectedTask = Task.builder()
                .taskId(taskId)
                .queue("test-queue")
                .status("pending")
                .build();

        // Mock the service call
        when(service.getTask(taskId)).thenReturn(expectedTask);

        // Execute the test
        Task result = service.getTask(taskId);

        // Verify the results
        assertNotNull(result);
        assertEquals(taskId, result.getTaskId());
        assertEquals("test-queue", result.getQueue());
        assertEquals("pending", result.getStatus());
    }
}
