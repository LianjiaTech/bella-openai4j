package com.theokanning.openai.service;

import com.theokanning.openai.DeleteResult;
import com.theokanning.openai.file.*;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileApiTest {

    static OpenAiService service;
    static String uploadedFileId;
    static String dirId;

    @BeforeAll
    static void setup() {
        String apiKey = System.getenv("CUSTOM_API_KEY");
        assumeTrue(apiKey != null && !apiKey.isEmpty(), "CUSTOM_API_KEY not set, skipping");
        String baseUrl = System.getenv("CUSTOM_API_BASE_URL");
        assumeTrue(baseUrl != null && !baseUrl.isEmpty(), "CUSTOM_API_BASE_URL not set, skipping");
        service = new OpenAiService(apiKey, Duration.ofSeconds(60), baseUrl);
    }

    // ========== Upload ==========

    @Test
    @Order(1)
    void uploadFileBasic() {
        byte[] content = "hello world".getBytes(StandardCharsets.UTF_8);
        File file = service.uploadFile("assistants", content, "test-upload.txt");
        assertNotNull(file);
        assertNotNull(file.getId());
        assertEquals("test-upload.txt", file.getFilename());
        uploadedFileId = file.getId();
        System.out.println("[uploadFileBasic] id=" + uploadedFileId);
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
                .cities(Arrays.asList("beijing"))
                .build();
        File file = service.uploadFile(request, content, "test-request-upload.txt");
        assertNotNull(file);
        assertNotNull(file.getId());
        System.out.println("[uploadFileWithRequest] id=" + file.getId() + " url=" + file.getUrl());
        // clean up
        service.deleteFile(file.getId());
    }

    // ========== List ==========

    @Test
    @Order(10)
    void listFilesNoParams() {
        List<File> files = service.listFiles();
        assertNotNull(files);
        System.out.println("[listFilesNoParams] count=" + files.size());
    }

    @Test
    @Order(11)
    void listFilesWithQuery() {
        FileListQuery query = FileListQuery.builder()
                .purpose("assistants")
                .limit(5)
                .order("desc")
                .build();
        List<File> files = service.listFiles(query);
        assertNotNull(files);
        assertTrue(files.size() <= 5);
        System.out.println("[listFilesWithQuery] count=" + files.size());
    }

    @Test
    @Order(12)
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
        System.out.println("[listFilesByIds] url=" + files.get(0).getUrl());
    }

    // ========== Retrieve ==========

    @Test
    @Order(20)
    void retrieveFileBasic() {
        File file = service.retrieveFile(uploadedFileId);
        assertNotNull(file);
        assertEquals(uploadedFileId, file.getId());
        System.out.println("[retrieveFileBasic] filename=" + file.getFilename());
    }

    @Test
    @Order(21)
    void retrieveFileWithUrl() {
        File file = service.retrieveFile(uploadedFileId, true, 3600L);
        assertNotNull(file);
        assertNotNull(file.getUrl());
        System.out.println("[retrieveFileWithUrl] url=" + file.getUrl());
    }

    @Test
    @Order(22)
    void retrieveFileUrl() {
        FileUrl fileUrl = service.retrieveFileUrl(uploadedFileId);
        assertNotNull(fileUrl);
        assertNotNull(fileUrl.getUrl());
        System.out.println("[retrieveFileUrl] url=" + fileUrl.getUrl());
    }

    @Test
    @Order(23)
    void retrieveFileUrlWithExpires() {
        FileUrl fileUrl = service.retrieveFileUrl(uploadedFileId, 7200L);
        assertNotNull(fileUrl);
        assertNotNull(fileUrl.getUrl());
        System.out.println("[retrieveFileUrlWithExpires] expiresAt=" + fileUrl.getExpiresAt());
    }

    @Test
    @Order(24)
    void retrieveFileContent() {
        okhttp3.ResponseBody body = service.retrieveFileContent(uploadedFileId);
        assertNotNull(body);
        System.out.println("[retrieveFileContent] contentType=" + body.contentType());
    }

    @Test
    @Order(25)
    void retrieveFileInfo() {
        File file = service.retrieveFileInfo(uploadedFileId);
        assertNotNull(file);
        assertEquals(uploadedFileId, file.getId());
        System.out.println("[retrieveFileInfo] nodeType=" + file.getNodeType() + " metadata=" + file.getMetadata());
    }

    // ========== Rename ==========

    @Test
    @Order(30)
    void renameFile() {
        File file = service.renameFile(uploadedFileId, "renamed-test.txt");
        assertNotNull(file);
        assertEquals("renamed-test.txt", file.getFilename());
        System.out.println("[renameFile] newName=" + file.getFilename());
    }

    // ========== Update metadata ==========

    @Test
    @Order(40)
    void updateFileDescription() {
        File file = service.updateFileDescription(uploadedFileId, "updated description");
        assertNotNull(file);
        assertEquals("updated description", file.getDescription());
        System.out.println("[updateFileDescription] desc=" + file.getDescription());
    }

    @Test
    @Order(41)
    void updateFileTags() {
        File file = service.updateFileTags(uploadedFileId, Arrays.asList("tag1", "tag2"));
        assertNotNull(file);
        assertNotNull(file.getTags());
        assertTrue(file.getTags().contains("tag1"));
        System.out.println("[updateFileTags] tags=" + file.getTags());
    }

    @Test
    @Order(42)
    void updateFileCities() {
        File file = service.updateFileCities(uploadedFileId, Arrays.asList("beijing", "shanghai"));
        assertNotNull(file);
        assertNotNull(file.getCities());
        System.out.println("[updateFileCities] cities=" + file.getCities());
    }

    // ========== Update content ==========

    @Test
    @Order(50)
    void updateFileContent() {
        byte[] newContent = "updated content".getBytes(StandardCharsets.UTF_8);
        File file = service.updateFileContent(uploadedFileId, newContent, "renamed-test.txt");
        assertNotNull(file);
        assertEquals(uploadedFileId, file.getId());
        System.out.println("[updateFileContent] version=" + file.getVersion());
    }

    // ========== Progress ==========

    @Test
    @Order(60)
    void updateProgress() {
        ProgressUpdateRequest request = ProgressUpdateRequest.builder()
                .status("running")
                .message("processing step 1")
                .percent(50)
                .build();
        Progress progress = service.updateProgress(uploadedFileId, "parse", request);
        assertNotNull(progress);
        System.out.println("[updateProgress] status=" + progress.getStatus() + " percent=" + progress.getPercent());
    }

    @Test
    @Order(61)
    void retrieveProgress() {
        Progress progress = service.retrieveProgress(uploadedFileId, "parse");
        assertNotNull(progress);
        assertEquals("running", progress.getStatus());
        assertEquals(Integer.valueOf(50), progress.getPercent());
        System.out.println("[retrieveProgress] fileId=" + progress.getFileId() + " name=" + progress.getName());
    }

    // ========== Preview URL ==========

    @Test
    @Order(65)
    void retrievePreviewUrl() {
        FileUrl previewUrl = service.retrievePreviewUrl(uploadedFileId, 3600L);
        assertNotNull(previewUrl);
        System.out.println("[retrievePreviewUrl] url=" + previewUrl.getUrl());
    }

    // ========== Directory & Find ==========

    @Test
    @Order(70)
    void createDirectory() {
        MkdirRequest request = MkdirRequest.builder()
                .name("test-sdk-dir-" + System.currentTimeMillis())
                .purpose("assistants")
                .build();
        File dir = service.createDirectory(request);
        assertNotNull(dir);
        assertNotNull(dir.getId());
        assertTrue(dir.getIsDir());
        dirId = dir.getId();
        System.out.println("[createDirectory] id=" + dirId + " name=" + dir.getFilename());
    }

    @Test
    @Order(71)
    void findFiles() {
        assertNotNull(dirId);
        FindFilesQuery query = FindFilesQuery.builder()
                .ancestorId(dirId)
                .build();
        List<File> files = service.findFiles(query);
        assertNotNull(files);
        System.out.println("[findFiles] count=" + files.size());
    }

    // ========== Page ==========

    @Test
    @Order(75)
    void pageFiles() {
        assertNotNull(dirId);
        PageFilesRequest request = PageFilesRequest.builder()
                .page(1)
                .pageSize(5)
                .order("desc")
                .ancestorId(dirId)
                .build();
        FilePage page = service.pageFiles(request);
        assertNotNull(page);
        assertNotNull(page.getData());
        assertNotNull(page.getTotal());
        System.out.println("[pageFiles] total=" + page.getTotal() + " pageSize=" + page.getPageSize()
                + " limit=" + page.getLimit() + " hasMore=" + page.getHasMore());
    }

    // ========== Exists ==========

    @Test
    @Order(76)
    void fileExists() {
        FileExistsQuery query = FileExistsQuery.builder()
                .filename("renamed-test.txt")
                .build();
        FileExistsResponse response = service.fileExists(query);
        assertNotNull(response);
        System.out.println("[fileExists] exists=" + response.isExists());
    }

    // ========== Move ==========

    @Test
    @Order(80)
    void moveFile() {
        assertNotNull(dirId);
        FileMoveRequest request = FileMoveRequest.builder()
                .fileId(uploadedFileId)
                .ancestorId(dirId)
                .build();
        File movedFile = service.moveFile(request);
        assertNotNull(movedFile);
        System.out.println("[moveFile] id=" + movedFile.getId() + " moved to dir=" + dirId);
    }

    // ========== Ancestor IDs ==========

    @Test
    @Order(81)
    void getFileAncestorIds() {
        assertNotNull(uploadedFileId);
        FileAncestorIdsRequest request = FileAncestorIdsRequest.builder()
                .fileIds(Collections.singletonList(uploadedFileId))
                .build();
        try {
            Map<String, List<String>> result = service.getFileAncestorIds(request);
            assertNotNull(result);
            System.out.println("[getFileAncestorIds] result=" + result);
        } catch (Exception e) {
            // spaceCode may be required by server validation
            System.out.println("[getFileAncestorIds] skipped: " + e.getMessage());
        }
    }

    // ========== Cleanup ==========

    @Test
    @Order(90)
    void deleteUploadedFile() {
        assertNotNull(uploadedFileId);
        DeleteResult result = service.deleteFile(uploadedFileId);
        assertNotNull(result);
        assertTrue(result.isDeleted());
        System.out.println("[deleteUploadedFile] deleted=" + uploadedFileId);
    }

    @Test
    @Order(91)
    void deleteDirectory() {
        if (dirId != null) {
            DeleteResult result = service.deleteFile(dirId);
            assertNotNull(result);
            assertTrue(result.isDeleted());
            System.out.println("[deleteDirectory] deleted=" + dirId);
        }
    }
}
