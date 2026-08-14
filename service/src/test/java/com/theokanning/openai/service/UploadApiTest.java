package com.theokanning.openai.service;

import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.upload.CompleteUploadRequest;
import com.theokanning.openai.upload.CreateUploadRequest;
import com.theokanning.openai.upload.Upload;
import com.theokanning.openai.upload.UploadPart;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UploadApiTest {

    static final long PART_SIZE = 5 * 1024 * 1024L;

    static final String SUFFIX = String.valueOf(System.currentTimeMillis());

    static OpenAiService service;
    static String uploadId;
    static List<String> partIds = new ArrayList<>();
    static byte[] content;

    @BeforeAll
    static void setup() {
        String apiKey = System.getenv("CUSTOM_API_KEY");
        assumeTrue(apiKey != null && !apiKey.isEmpty(), "CUSTOM_API_KEY not set, skipping");
        String baseUrl = System.getenv("CUSTOM_API_BASE_URL");
        assumeTrue(baseUrl != null && !baseUrl.isEmpty(), "CUSTOM_API_BASE_URL not set, skipping");
        service = new OpenAiService(apiKey, Duration.ofSeconds(120), baseUrl);
        // 5MB + 5MB + 2MB, three parts
        content = new byte[(int) (2 * PART_SIZE + 2 * 1024 * 1024)];
        new Random(42).nextBytes(content);
    }

    @Test
    @Order(1)
    void createUpload() {
        CreateUploadRequest request = CreateUploadRequest.builder()
                .filename("large-upload-test-" + SUFFIX + ".bin")
                .purpose("assistants")
                .bytes((long) content.length)
                .mimeType("application/octet-stream")
                .build();
        Upload upload = service.createUpload(request);
        assertNotNull(upload.getId());
        assertEquals("upload", upload.getObject());
        assertEquals("pending", upload.getStatus());
        assertEquals(content.length, upload.getBytes());
        assertNotNull(upload.getPartSizeMin());
        uploadId = upload.getId();
        System.out.println("[createUpload] id=" + uploadId);
    }

    @Test
    @Order(2)
    void addParts() {
        int partNumber = 1;
        for (int offset = 0; offset < content.length; offset += PART_SIZE) {
            int end = (int) Math.min(content.length, offset + PART_SIZE);
            byte[] chunk = java.util.Arrays.copyOfRange(content, offset, end);
            UploadPart part = service.addUploadPart(uploadId, partNumber, chunk);
            assertNotNull(part.getId());
            assertEquals("upload.part", part.getObject());
            assertEquals(partNumber, part.getPartNumber());
            assertEquals(chunk.length, part.getSize());
            partIds.add(part.getId());
            partNumber++;
        }
        assertEquals(3, partIds.size());
        System.out.println("[addParts] partIds=" + partIds);
    }

    @Test
    @Order(3)
    void listParts() {
        OpenAiResponse<UploadPart> parts = service.listUploadParts(uploadId);
        assertNotNull(parts.getData());
        assertEquals(3, parts.getData().size());
    }

    @Test
    @Order(4)
    void completeUpload() {
        Upload completed = service.completeUpload(uploadId, new CompleteUploadRequest(partIds));
        assertEquals("completed", completed.getStatus());
        assertNotNull(completed.getFile());
        assertNotNull(completed.getFile().getId());
        assertEquals(content.length, completed.getFile().getBytes());
        System.out.println("[completeUpload] fileId=" + completed.getFile().getId());
    }

    @Test
    @Order(5)
    void cancelUpload() {
        CreateUploadRequest request = CreateUploadRequest.builder()
                .filename("cancel-test-" + SUFFIX + ".bin")
                .purpose("assistants")
                .bytes(1024L)
                .mimeType("application/octet-stream")
                .build();
        Upload upload = service.createUpload(request);
        Upload cancelled = service.cancelUpload(upload.getId());
        assertEquals("cancelled", cancelled.getStatus());
    }

    @Test
    @Order(6)
    void uploadFileInParts() throws IOException {
        Path file = Paths.get("target", "in-parts-" + SUFFIX + ".bin");
        Files.write(file, content);
        CreateUploadRequest request = CreateUploadRequest.builder()
                .purpose("assistants")
                .mimeType("application/octet-stream")
                .build();
        Upload completed = service.uploadFileInParts(request, file, PART_SIZE);
        assertEquals("completed", completed.getStatus());
        assertNotNull(completed.getFile());
        assertEquals(file.getFileName().toString(), completed.getFile().getFilename());
        assertEquals(content.length, completed.getFile().getBytes());
        System.out.println("[uploadFileInParts] fileId=" + completed.getFile().getId());
    }

    @Test
    @Order(7)
    void uploadStreamInParts() {
        CreateUploadRequest request = CreateUploadRequest.builder()
                .filename("in-parts-stream-" + SUFFIX + ".bin")
                .purpose("assistants")
                .mimeType("application/octet-stream")
                .build();
        Upload completed = service.uploadStreamInParts(
                request, new java.io.ByteArrayInputStream(content), content.length, PART_SIZE);
        assertEquals("completed", completed.getStatus());
        assertNotNull(completed.getFile());
        assertEquals(content.length, completed.getFile().getBytes());
        System.out.println("[uploadStreamInParts] fileId=" + completed.getFile().getId());
    }

    @Test
    @Order(8)
    void uploadStreamInPartsSizeMismatch() {
        CreateUploadRequest request = CreateUploadRequest.builder()
                .filename("in-parts-mismatch-" + SUFFIX + ".bin")
                .purpose("assistants")
                .mimeType("application/octet-stream")
                .build();
        // declare more bytes than the stream provides
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> service.uploadStreamInParts(
                request, new java.io.ByteArrayInputStream(new byte[1024]), 2048, PART_SIZE));
        assertTrue(e.getMessage().contains("declared"));
    }
}
