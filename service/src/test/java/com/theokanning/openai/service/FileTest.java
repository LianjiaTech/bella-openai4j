package com.theokanning.openai.service;

import com.theokanning.openai.DeleteResult;
import com.theokanning.openai.file.File;
import com.theokanning.openai.file.FileListRequest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileTest {

    OpenAiService service = new OpenAiService();

    @Test
    @Order(1)
    void uploadFile() throws Exception {
        String filePath = "src/test/resources/fine-tuning-data.jsonl";
        File file = service.uploadFile("fine-tune", filePath);
        String fileId = file.getId();
        assertEquals("fine-tune", file.getPurpose());
        assertEquals("fine-tuning-data.jsonl", file.getFilename());
        // wait for file to be processed
        TimeUnit.SECONDS.sleep(10);

        List<File> files = service.listFiles();
        assertTrue(files.stream().anyMatch(fileItem -> fileItem.getId().equals(fileId)));
        file = service.retrieveFile(fileId);
        assertEquals("fine-tuning-data.jsonl", file.getFilename());
        String fileBytesToString = service.retrieveFileContent(fileId).string();
        String contents = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        assertEquals(contents, fileBytesToString);
        DeleteResult result = service.deleteFile(fileId);
        assertTrue(result.isDeleted());
    }

    @Test
    @Order(2)
    void uploadFileStream() throws Exception {
        String filePath = "batch-task-data.jsonl";
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(filePath);
        File file = service.uploadFile("fine-tune", resourceAsStream, "batch-task-data.jsonl");
        String fileId = file.getId();
        assertEquals("fine-tune", file.getPurpose());
        assertEquals(filePath, file.getFilename());
        // wait for file to be processed
        TimeUnit.SECONDS.sleep(10);

        List<File> files = service.listFiles();
        assertTrue(files.stream().anyMatch(fileItem -> fileItem.getId().equals(fileId)));
        file = service.retrieveFile(fileId);
        assertEquals(filePath, file.getFilename());
        String fileBytesToString = service.retrieveFileContent(fileId).string();
        String contents = new String(Files.readAllBytes(new java.io.File(getClass().getClassLoader().getResource(filePath).getFile()).toPath()), StandardCharsets.UTF_8);
        assertEquals(contents, fileBytesToString);
        DeleteResult result = service.deleteFile(fileId);
        assertTrue(result.isDeleted());
    }

    @Test
    @Order(3)
    void listFilesWithRequest() {
        // 首先上传一些测试文件
        String filePath1 = "src/test/resources/fine-tuning-data.jsonl";
        String filePath2 = "batch-task-data.jsonl";
        
        File file1 = service.uploadFile("fine-tune", filePath1);
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(filePath2);
        File file2 = service.uploadFile("fine-tune", resourceAsStream, "batch-task-data.jsonl");
        
        String fileId1 = file1.getId();
        String fileId2 = file2.getId();
        
        try {
            // 测试使用 FileListRequest 查询指定文件
            FileListRequest request = FileListRequest.builder()
                    .fileIds(Arrays.asList(fileId1, fileId2))
                    .getUrl(true)
                    .expires(3600L)
                    .build();
            
            List<File> files = service.listFiles(request);
            
            // 验证结果
            assertNotNull(files);
            assertEquals(2, files.size());
            assertTrue(files.stream().anyMatch(f -> f.getId().equals(fileId1)));
            assertTrue(files.stream().anyMatch(f -> f.getId().equals(fileId2)));
            
            // 测试只查询一个文件
            FileListRequest singleFileRequest = FileListRequest.builder()
                    .fileIds(Arrays.asList(fileId1))
                    .getUrl(false)
                    .build();
            
            List<File> singleFileResult = service.listFiles(singleFileRequest);
            assertNotNull(singleFileResult);
            assertEquals(1, singleFileResult.size());
            assertEquals(fileId1, singleFileResult.get(0).getId());
            
        } finally {
            // 清理测试文件
            service.deleteFile(fileId1);
            service.deleteFile(fileId2);
        }
    }
}
