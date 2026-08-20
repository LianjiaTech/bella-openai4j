package example;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.upload.CreateUploadRequest;
import com.theokanning.openai.upload.Upload;
import com.theokanning.openai.upload.UploadPart;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * End-to-end check against a local bella-knowledge instance:
 * creating an Upload without declaring bytes, then completing it.
 */
public class UploadWithoutBytesExample {

    public static void main(String... args) {
        String baseUrl = System.getProperty("baseUrl", "http://127.0.0.1:18081/v1/");
        String token = System.getProperty("token", "local-dev");
        OpenAiService service = new OpenAiService(token, Duration.ofSeconds(60), baseUrl);

        byte[] content = "hello from bella-openai4j without declared bytes\n".getBytes(StandardCharsets.UTF_8);

        // 1. create WITHOUT bytes
        Upload upload = service.createUpload(CreateUploadRequest.builder()
                .filename("no-bytes-" + System.currentTimeMillis() + ".txt")
                .purpose("temp")
                .mimeType("text/plain")
                .build());
        System.out.println("[create-no-bytes] id=" + upload.getId() + " status=" + upload.getStatus() + " bytes=" + upload.getBytes());

        // 2. add one part
        UploadPart part = service.addUploadPart(upload.getId(), 1, content);
        System.out.println("[part] id=" + part.getId() + " size=" + part.getSize());

        // 3. complete — file size should come from the actual uploaded bytes
        Upload done = service.completeUpload(upload.getId());
        System.out.println("[complete] status=" + done.getStatus() + " bytes=" + done.getBytes()
                + " fileId=" + (done.getFile() == null ? null : done.getFile().getId())
                + " fileBytes=" + (done.getFile() == null ? null : done.getFile().getBytes()));
        if(done.getBytes() == null || done.getBytes() != content.length) {
            throw new IllegalStateException("expected completed bytes " + content.length + " but got " + done.getBytes());
        }

        // 4. control: declared bytes mismatch must still be rejected
        Upload declared = service.createUpload(CreateUploadRequest.builder()
                .filename("declared-" + System.currentTimeMillis() + ".txt")
                .purpose("temp")
                .bytes((long) content.length + 5)
                .mimeType("text/plain")
                .build());
        service.addUploadPart(declared.getId(), 1, content);
        try {
            service.completeUpload(declared.getId());
            throw new IllegalStateException("declared-bytes mismatch was NOT rejected");
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("[declared-mismatch] correctly rejected: " + e.getMessage());
        }

        System.out.println("ALL CHECKS PASSED");
    }
}
