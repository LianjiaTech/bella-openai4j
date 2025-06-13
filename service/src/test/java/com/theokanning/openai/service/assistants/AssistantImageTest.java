package com.theokanning.openai.service.assistants;

import com.theokanning.openai.DeleteResult;
import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.assistants.assistant.Assistant;
import com.theokanning.openai.assistants.assistant.AssistantRequest;
import com.theokanning.openai.assistants.message.Message;
import com.theokanning.openai.assistants.message.MessageListSearchParameters;
import com.theokanning.openai.assistants.message.MessageRequest;
import com.theokanning.openai.assistants.run.CreateThreadAndRunRequest;
import com.theokanning.openai.assistants.run.Run;
import com.theokanning.openai.assistants.thread.ThreadRequest;
import com.theokanning.openai.file.File;
import com.theokanning.openai.service.OpenAiService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author LiangTao
 * @date 2024年05月11 14:51
 **/
public class AssistantImageTest {
    static OpenAiService service = new OpenAiService();
    static String assistantId;
    static String threadId;

    @BeforeAll
    static void initial() {
        AssistantRequest assistantRequest = AssistantRequest.builder()
                .model("gpt-4o-mini").name("weather assistant")
                .instructions("你乐于助人,帮助用户")
                .temperature(1D)
                .build();
        Assistant assistant = service.createAssistant(assistantRequest);
        assistantId = assistant.getId();
    }

    @AfterAll
    static void deleteTestData() {
        try {
            DeleteResult deleteResult = service.deleteAssistant(assistantId);
            assertTrue(deleteResult.isDeleted());
        } catch (Exception e) {
            // ignore
        }
        try {
            DeleteResult deleteResult = service.deleteThread(threadId);
            assertTrue(deleteResult.isDeleted());
        } catch (Exception e) {
            // ignore
        }
    }

    @Test
    void testImageByImageUrl() {
        Run run = service.createThreadAndRun(CreateThreadAndRunRequest.builder()
                .assistantId(assistantId)
                .thread(ThreadRequest.builder()
                        .messages(Collections.singletonList(
                                MessageRequest.builder()
                                        .urlImageMessage("这个图片里面描述了什么?","https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg")
                                        .build()))
                        .build())
                .build());
        threadId= run.getThreadId();
        Run retrievedRun = service.retrieveRun(run.getThreadId(), run.getId());
        while (!(retrievedRun.getStatus().equals("completed"))
                && !(retrievedRun.getStatus().equals("failed"))
                && !(retrievedRun.getStatus().equals("expired"))
                && !(retrievedRun.getStatus().equals("incomplete"))
                && !(retrievedRun.getStatus().equals("requires_action"))) {
            retrievedRun = service.retrieveRun(threadId, run.getId());
        }
        OpenAiResponse<Message> response = service.listMessages(threadId, MessageListSearchParameters.builder().runId(retrievedRun.getId()).build());
        List<Message> data = response.getData();
        assertTrue(!data.isEmpty());
    }

    @Test
   void  testImageByImageFile(){
        File file = service.uploadFile("assistants", "src/test/resources/penguin.png");
        Run run = service.createThreadAndRun(CreateThreadAndRunRequest.builder()
                .assistantId(assistantId)
                .thread(ThreadRequest.builder()
                        .messages(Collections.singletonList(
                                MessageRequest.builder()
                                        .fileImageMessageWithDetail("这个图片里面描述了什么?","high", file.getId())
                                        .build()))
                        .build())
                .build());
        threadId= run.getThreadId();
        Run retrievedRun = service.retrieveRun(run.getThreadId(), run.getId());
        while (!(retrievedRun.getStatus().equals("completed"))
                && !(retrievedRun.getStatus().equals("failed"))
                && !(retrievedRun.getStatus().equals("expired"))
                && !(retrievedRun.getStatus().equals("incomplete"))
                && !(retrievedRun.getStatus().equals("requires_action"))) {
            retrievedRun = service.retrieveRun(threadId, run.getId());
        }
        OpenAiResponse<Message> response = service.listMessages(threadId, MessageListSearchParameters.builder().runId(retrievedRun.getId()).build());
        List<Message> data = response.getData();
        assertTrue(!data.isEmpty());
        service.deleteFile(file.getId());
    }
}
