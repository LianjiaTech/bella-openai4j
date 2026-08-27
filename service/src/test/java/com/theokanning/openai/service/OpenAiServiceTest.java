package com.theokanning.openai.service;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.upload.CreateUploadRequest;
import com.theokanning.openai.upload.UploadPart;
import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OpenAiServiceTest {

    @Test
    void assertTokenNotNull() {
        String token = null;
        assertThrows(NullPointerException.class, () -> new OpenAiService(token));
    }

    @Test
    void executeHappyPath() {
        CompletionResult expected = new CompletionResult();
        Single<CompletionResult> single = Single.just(expected);

        CompletionResult actual = OpenAiService.execute(single);
        assertEquals(expected, actual);
    }

    @Test
    void executeParseHttpError() {
        String errorBody = "{\"error\":{\"message\":\"Invalid auth token\",\"type\":\"type\",\"param\":\"param\",\"code\":\"code\"}}";
        HttpException httpException = createException(errorBody, 401);
        Single<CompletionResult> single = Single.error(httpException);

        OpenAiHttpException exception = assertThrows(OpenAiHttpException.class, () -> OpenAiService.execute(single));

        assertEquals("Invalid auth token", exception.getMessage());
        assertEquals("type", exception.type);
        assertEquals("param", exception.param);
        assertEquals("code", exception.code);
        assertEquals(401, exception.statusCode);
    }

    @Test
    void executeParseUnknownProperties() {
        // error body contains one unknown property and no message
        String errorBody = "{\"error\":{\"unknown\":\"Invalid auth token\",\"type\":\"type\",\"param\":\"param\",\"code\":\"code\"}}";
        HttpException httpException = createException(errorBody, 401);
        Single<CompletionResult> single = Single.error(httpException);

        OpenAiHttpException exception = assertThrows(OpenAiHttpException.class, () -> OpenAiService.execute(single));
        assertNull(exception.getMessage());
        assertEquals("type", exception.type);
        assertEquals("param", exception.param);
        assertEquals("code", exception.code);
        assertEquals(401, exception.statusCode);
    }

    @Test
    void executeNullErrorBodyThrowOriginalError() {
        // exception with a successful response creates an error without an error body
        HttpException httpException = new HttpException(Response.success(new CompletionResult()));
        Single<CompletionResult> single = Single.error(httpException);

        HttpException exception = assertThrows(HttpException.class, () -> OpenAiService.execute(single));
    }

    @Test
    void addUploadPartSendsOnlyRequestedBufferRange() throws Exception {
        OpenAiApi api = mock(OpenAiApi.class);
        UploadPart expected = new UploadPart();
        expected.setId("part-1");
        when(api.addUploadPart(eq("upload-1"), any(), any())).thenReturn(Single.just(expected));

        OpenAiService service = new OpenAiService(api);
        byte[] buffer = new byte[]{99, 10, 20, 30, 88};
        UploadPart actual = service.addUploadPart("upload-1", 1, buffer, 1, 3);

        ArgumentCaptor<MultipartBody.Part> dataPart = ArgumentCaptor.forClass(MultipartBody.Part.class);
        verify(api).addUploadPart(eq("upload-1"), any(), dataPart.capture());
        Buffer sink = new Buffer();
        dataPart.getValue().body().writeTo(sink);

        assertSame(expected, actual);
        assertEquals(3, dataPart.getValue().body().contentLength());
        assertArrayEquals(new byte[]{10, 20, 30}, sink.readByteArray());
    }

    @Test
    void uploadStreamInPartsRejectsPartSizeLargerThanJavaArray() {
        OpenAiService service = new OpenAiService(mock(OpenAiApi.class));
        CreateUploadRequest request = CreateUploadRequest.builder().filename("large.bin").build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.uploadStreamInParts(
                        request,
                        new ByteArrayInputStream(new byte[0]),
                        0,
                        (long) Integer.MAX_VALUE + 1));

        assertEquals("partSize must not exceed " + Integer.MAX_VALUE, exception.getMessage());
    }

    private HttpException createException(String errorBody, int code) {
        ResponseBody body = ResponseBody.create(MediaType.get("application/json"), errorBody);
        Response<Void> response = Response.error(code, body);
        return new HttpException(response);
    }
}
