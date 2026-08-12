package com.theokanning.openai.service;

import com.theokanning.openai.DeleteResult;
import com.theokanning.openai.file.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnabledIfEnvironmentVariable(named = "CUSTOM_API_KEY", matches = ".+")
@Tag("integration")
public class FileApiTest {

    static OpenAiService service;
    static String uploadedFileId;

    @BeforeAll
    static void setup() {
        String apiKey = System.getenv("CUSTOM_API_KEY");
        assumeTrue(apiKey != null && !apiKey.isEmpty(), "CUSTOM_API_KEY not set, skipping");
        String baseUrl = System.getenv("CUSTOM_API_BASE_URL");
        assumeTrue(baseUrl != null && !baseUrl.isEmpty(), "CUSTOM_API_BASE_URL not set, skipping");
        service = new OpenAiService(apiKey, Duration.ofSeconds(60), baseUrl);
    }

    @Test
    @Order(1)
    void uploadFileBasic() {
        byte[] content = "hello world".getBytes(StandardCharsets.UTF_8);
        File file = service.uploadFile("assistants", content, "test-upload.txt");
        assertNotNull(file);
        assertNotNull(file.getId());
        assertEquals("test-upload.txt", file.getFilename());
        uploadedFileId = file.getId();
    }

    @Test
    @Order(2)
    void uploadFileWithRequest() {
        byte[] content = "upload with request".getBytes(StandardCharsets.UTF_8);
        FileUploadRequest request = FileUploadRequest.builder()
                .purpose("assistants")
                .getUrl(true)
                .expires(3600L)
                .description("test file with request")
                .tags(Arrays.asList("test", "sdk"))
                .build();
        File file = service.uploadFile(request, content, "test-request-upload.txt");
        assertNotNull(file);
        assertNotNull(file.getId());
        // clean up
        service.deleteFile(file.getId());
    }

    @Test
    @Order(3)
    void listFilesNoParams() {
        List<File> files = service.listFiles();
        assertNotNull(files);
    }

    @Test
    @Order(4)
    void listFilesWithQuery() {
        FileListQuery query = FileListQuery.builder()
                .purpose("assistants")
                .limit(5)
                .order("desc")
                .build();
        List<File> files = service.listFiles(query);
        assertNotNull(files);
        assertTrue(files.size() <= 5);
    }

    @Test
    @Order(5)
    void listFilesByIds() {
        assertNotNull(uploadedFileId, "uploadedFileId should be set from order 1");
        FileListRequest request = FileListRequest.builder()
                .fileIds(Collections.singletonList(uploadedFileId))
                .getUrl(true)
                .expires(3600L)
                .build();
        List<File> files = service.listFiles(request);
        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals(uploadedFileId, files.get(0).getId());
        assertNotNull(files.get(0).getUrl());
    }

    @Test
    @Order(6)
    void retrieveFileBasic() {
        File file = service.retrieveFile(uploadedFileId);
        assertNotNull(file);
        assertEquals(uploadedFileId, file.getId());
    }

    @Test
    @Order(7)
    void retrieveFileWithUrl() {
        File file = service.retrieveFile(uploadedFileId, true, 3600L);
        assertNotNull(file);
        assertEquals(uploadedFileId, file.getId());
        assertNotNull(file.getUrl());
    }

    @Test
    @Order(8)
    void retrieveFileUrl() {
        FileUrl fileUrl = service.retrieveFileUrl(uploadedFileId);
        assertNotNull(fileUrl);
        assertNotNull(fileUrl.getUrl());
    }

    @Test
    @Order(9)
    void retrieveFileUrlWithExpires() {
        FileUrl fileUrl = service.retrieveFileUrl(uploadedFileId, 7200L);
        assertNotNull(fileUrl);
        assertNotNull(fileUrl.getUrl());
    }

    @Test
    @Order(10)
    void retrieveFileContent() {
        okhttp3.ResponseBody body = service.retrieveFileContent(uploadedFileId);
        assertNotNull(body);
    }

    @Test
    @Order(11)
    void retrieveFileInfo() {
        File file = service.retrieveFileInfo(uploadedFileId);
        assertNotNull(file);
        assertEquals(uploadedFileId, file.getId());
    }

    @Test
    @Order(12)
    void renameFile() {
        File file = service.renameFile(uploadedFileId, "renamed-test.txt");
        assertNotNull(file);
        assertEquals("renamed-test.txt", file.getFilename());
    }

    @Test
    @Order(13)
    void updateFileDescription() {
        File file = service.updateFileDescription(uploadedFileId, "updated description");
        assertNotNull(file);
        assertEquals("updated description", file.getDescription());
    }

    @Test
    @Order(14)
    void updateFileTags() {
        File file = service.updateFileTags(uploadedFileId, Arrays.asList("tag1", "tag2"));
        assertNotNull(file);
        assertNotNull(file.getTags());
        assertTrue(file.getTags().contains("tag1"));
    }

    @Test
    @Order(15)
    void updateFileCities() {
        File file = service.updateFileCities(uploadedFileId, Arrays.asList("beijing", "shanghai"));
        assertNotNull(file);
        assertNotNull(file.getCities());
    }

    @Test
    @Order(20)
    void pageFiles() {
        PageFilesRequest request = PageFilesRequest.builder()
                .page(1)
                .pageSize(5)
                .build();
        FilePage page = service.pageFiles(request);
        assertNotNull(page);
        assertNotNull(page.getData());
        assertNotNull(page.getTotal());
    }

    @Test
    @Order(21)
    void createDirectoryAndFindFiles() {
        MkdirRequest request = MkdirRequest.builder()
                .name("test-sdk-dir")
                .purpose("assistants")
                .build();
        File dir = service.createDirectory(request);
        assertNotNull(dir);
        assertNotNull(dir.getId());
        assertTrue(dir.getIsDir());

        // find files in the newly created directory
        FindFilesQuery query = FindFilesQuery.builder()
                .ancestorId(dir.getId())
                .build();
        List<File> files = service.findFiles(query);
        assertNotNull(files);

        // clean up
        service.deleteFile(dir.getId());
    }

    @Test
    @Order(22)
    void updateProgress() {
        ProgressUpdateRequest request = ProgressUpdateRequest.builder()
                .status("running")
                .message("processing step 1")
                .percent(50)
                .build();
        Progress progress = service.updateProgress(uploadedFileId, "parse", request);
        assertNotNull(progress);
    }

    @Test
    @Order(23)
    void retrieveProgress() {
        Progress progress = service.retrieveProgress(uploadedFileId, "parse");
        assertNotNull(progress);
    }

    @Test
    @Order(30)
    void moveFile() {
        // create a dir first to move the file into
        MkdirRequest mkdirRequest = MkdirRequest.builder()
                .name("move-target-dir")
                .purpose("assistants")
                .build();
        File dir = service.createDirectory(mkdirRequest);

        FileMoveRequest request = FileMoveRequest.builder()
                .fileId(uploadedFileId)
                .ancestorId(dir.getId())
                .build();
        File movedFile = service.moveFile(request);
        assertNotNull(movedFile);

        // clean up dir
        service.deleteFile(dir.getId());
    }

    @Test
    @Order(90)
    void deleteFile() {
        DeleteResult result = service.deleteFile(uploadedFileId);
        assertNotNull(result);
        assertTrue(result.isDeleted());
    }
}
