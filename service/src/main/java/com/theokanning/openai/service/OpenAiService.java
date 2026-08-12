package com.theokanning.openai.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.theokanning.openai.DeleteResult;
import com.theokanning.openai.ListSearchParameters;
import com.theokanning.openai.OpenAiError;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.assistants.assistant.Assistant;
import com.theokanning.openai.assistants.assistant.AssistantRequest;
import com.theokanning.openai.assistants.assistant.ModifyAssistantRequest;
import com.theokanning.openai.assistants.assistant.VectorStoreFileRequest;
import com.theokanning.openai.assistants.message.Message;
import com.theokanning.openai.assistants.message.MessageListSearchParameters;
import com.theokanning.openai.assistants.message.MessageRequest;
import com.theokanning.openai.assistants.message.ModifyMessageRequest;
import com.theokanning.openai.assistants.run.CreateThreadAndRunRequest;
import com.theokanning.openai.assistants.run.ModifyRunRequest;
import com.theokanning.openai.assistants.run.Run;
import com.theokanning.openai.assistants.run.RunCreateRequest;
import com.theokanning.openai.assistants.run.SubmitToolOutputsRequest;
import com.theokanning.openai.assistants.run_step.RunStep;
import com.theokanning.openai.assistants.thread.Thread;
import com.theokanning.openai.assistants.thread.ThreadRequest;
import com.theokanning.openai.assistants.vector_store.ModifyVectorStoreRequest;
import com.theokanning.openai.assistants.vector_store.VectorStore;
import com.theokanning.openai.assistants.vector_store.VectorStoreRequest;
import com.theokanning.openai.assistants.vector_store_file.VectorStoreFile;
import com.theokanning.openai.assistants.vector_store_file_batch.VectorStoreFilesBatch;
import com.theokanning.openai.assistants.vector_store_file_batch.VectorStoreFilesBatchRequest;
import com.theokanning.openai.audio.CreateSpeechRequest;
import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.audio.CreateTranslationRequest;
import com.theokanning.openai.audio.TranscriptionResult;
import com.theokanning.openai.audio.TranslationResult;
import com.theokanning.openai.batch.Batch;
import com.theokanning.openai.batch.BatchRequest;
import com.theokanning.openai.billing.BillingUsage;
import com.theokanning.openai.billing.Subscription;
import com.theokanning.openai.client.AuthenticationInterceptor;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.completion.CompletionChunk;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.completion.chat.AssistantMessage;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatFunction;
import com.theokanning.openai.completion.chat.ChatFunctionCall;
import com.theokanning.openai.completion.chat.ChatToolCall;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.file.*;
import com.theokanning.openai.fine_tuning.FineTuningEvent;
import com.theokanning.openai.fine_tuning.FineTuningJob;
import com.theokanning.openai.fine_tuning.FineTuningJobCheckpoint;
import com.theokanning.openai.fine_tuning.FineTuningJobRequest;
import com.theokanning.openai.image.CreateImageEditRequest;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.CreateImageVariationRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.model.Model;
import com.theokanning.openai.moderation.ModerationRequest;
import com.theokanning.openai.moderation.ModerationResult;
import com.theokanning.openai.queue.EventbusConfig;
import com.theokanning.openai.queue.Put;
import com.theokanning.openai.queue.Queue;
import com.theokanning.openai.queue.Register;
import com.theokanning.openai.queue.Take;
import com.theokanning.openai.queue.Task;
import com.theokanning.openai.service.assistant_stream.AssistantResponseBodyCallback;
import com.theokanning.openai.service.assistant_stream.AssistantSSE;
import com.theokanning.openai.service.response_stream.ResponseResponseBodyCallback;
import com.theokanning.openai.service.response_stream.ResponseSSE;
import com.theokanning.openai.web.WebCrawlRequest;
import com.theokanning.openai.web.WebCrawlResponse;
import com.theokanning.openai.web.WebExtractRequest;
import com.theokanning.openai.web.WebExtractResponse;
import com.theokanning.openai.web.WebSearchRequest;
import com.theokanning.openai.web.WebSearchResponse;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class OpenAiService {

    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1/";

    public static final String API_BASE_URL_ENV = "OPENAI_API_BASE_URL";

    public static final String API_KEY_ENV = "OPENAI_API_KEY";

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final ObjectMapper mapper = defaultObjectMapper();

    private final OpenAiApi api;
    private final ExecutorService executorService;

    /**
     * Creates a new OpenAiService that wraps OpenAiApi,user OPENAI_API_KEY from environment variable
     */
    public OpenAiService() {
        this(System.getenv(API_KEY_ENV));
    }

    public OpenAiService(Duration timeout) {
        this(System.getenv(API_KEY_ENV), timeout);
    }

    /**
     * Creates a new OpenAiService that wraps OpenAiApi
     *
     * @param token OpenAi token string "sk-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
     */
    public OpenAiService(final String token) {
        this(token, DEFAULT_TIMEOUT, System.getenv(API_BASE_URL_ENV) != null ? System.getenv(API_BASE_URL_ENV) : DEFAULT_BASE_URL);
    }

    public OpenAiService(final String token, final String baseUrl) {
        this(token, DEFAULT_TIMEOUT, baseUrl);
    }

    /**
     * Creates a new OpenAiService that wraps OpenAiApi
     *
     * @param token   OpenAi token string "sk-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
     * @param timeout http read timeout, Duration.ZERO means no timeout
     */
    public OpenAiService(final String token, final Duration timeout) {
        this(token, timeout, System.getenv(API_BASE_URL_ENV) != null ? System.getenv(API_BASE_URL_ENV) : DEFAULT_BASE_URL);
    }

    /**
     * Creates a new OpenAiService that wraps OpenAiApi
     *
     * @param token   OpenAi token string "sk-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
     * @param timeout http read timeout, Duration.ZERO means no timeout
     * @param baseUrl OpenAi API base URL, default is "https://api.openai.com/v1/"
     */
    public OpenAiService(final String token, final Duration timeout, String baseUrl) {
        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(token, timeout);
        Retrofit retrofit = defaultRetrofit(client, mapper, baseUrl);

        this.api = retrofit.create(OpenAiApi.class);
        this.executorService = client.dispatcher().executorService();
    }

    /**
     * Creates a new OpenAiService that wraps OpenAiApi.
     * Use this if you need more customization, but use OpenAiService(api, executorService) if you use streaming and
     * want to shut down instantly
     *
     * @param api OpenAiApi instance to use for all methods
     */
    public OpenAiService(final OpenAiApi api) {
        this.api = api;
        this.executorService = null;
    }

    /**
     * Creates a new OpenAiService that wraps OpenAiApi.
     * The ExecutorService must be the one you get from the client you created the api with
     * otherwise shutdownExecutor() won't work.
     * <p>
     * Use this if you need more customization.
     *
     * @param api             OpenAiApi instance to use for all methods
     * @param executorService the ExecutorService from client.dispatcher().executorService()
     */
    public OpenAiService(final OpenAiApi api, final ExecutorService executorService) {
        this.api = api;
        this.executorService = executorService;
    }

    public List<Model> listModels() {
        return execute(api.listModels()).data;
    }

    public Model getModel(String modelId) {
        return execute(api.getModel(modelId));
    }

    public static OpenAiApi buildApi(String token, Duration timeout) {
        return buildApi(token, timeout, System.getenv(API_BASE_URL_ENV) != null ? System.getenv(API_BASE_URL_ENV) : DEFAULT_BASE_URL);
    }

    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.addMixIn(ChatFunction.class, ChatFunctionMixIn.class);
        return mapper;
    }

    public ChatCompletionResult createChatCompletion(ChatCompletionRequest request) {
        return execute(api.createChatCompletion(request));
    }

    public Flowable<ChatCompletionChunk> streamChatCompletion(ChatCompletionRequest request) {
        request.setStream(true);
        return stream(api.createChatCompletionStream(request), ChatCompletionChunk.class, new BiConsumer<ChatCompletionChunk, SSE>() {
            @Override
            public void accept(ChatCompletionChunk chatCompletionChunk, SSE sse) {
                chatCompletionChunk.setSource(sse.getData());
            }
        }, new Supplier<ChatCompletionChunk>() {
            @Override
            public ChatCompletionChunk get() {
                return new ChatCompletionChunk();
            }
        });
    }


    public EmbeddingResult createEmbeddings(EmbeddingRequest request) {
        return execute(api.createEmbeddings(request));
    }

    @Deprecated
    public CompletionResult createCompletion(CompletionRequest request) {
        return execute(api.createCompletion(request));
    }

    public List<File> listFiles() {
        return execute(api.listFiles()).data;
    }

    public List<File> listFiles(FileListQuery query) {
        Map<String, Object> queryMap = mapper.convertValue(query, new TypeReference<Map<String, Object>>() {});
        queryMap.values().removeIf(Objects::isNull);
        return execute(api.listFiles(queryMap)).data;
    }

    public List<File> listFiles(FileListRequest request) {
        return execute(api.listFiles(request));
    }

    public DeleteResult deleteFile(String fileId) {
        return execute(api.deleteFile(fileId));
    }

    public File retrieveFile(String fileId) {
        return execute(api.retrieveFile(fileId));
    }

    public File retrieveFile(String fileId, Boolean getUrl, Long expires) {
        return execute(api.retrieveFile(fileId, getUrl, expires));
    }

    public File renameFile(String fileId, String filename) {
        return execute(api.renameFile(fileId, filename));
    }

    public File updateFileContent(String fileId, byte[] bytes, String filename) {
        RequestBody fileIdBody = RequestBody.create(MultipartBody.FORM, fileId);
        RequestBody fileBody = RequestBody.create(FileUtils.extraMediaType(filename), bytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", filename, fileBody);
        return execute(api.updateFileContent(fileIdBody, body));
    }

    public File updateFileContent(String fileId, Path filepath) {
        try (InputStream inputStream = Files.newInputStream(filepath)) {
            return updateFileContent(fileId, inputStream, filepath.getFileName().toString());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to update file content: " + filepath, e);
        }
    }

    public File updateFileContent(String fileId, InputStream inputStream, String filename) {
        RequestBody fileIdBody = RequestBody.create(MultipartBody.FORM, fileId);
        RequestBody fileBody = createStreamRequestBody(inputStream, filename);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", filename, fileBody);
        return execute(api.updateFileContent(fileIdBody, body));
    }

    public ResponseBody retrieveFileContent(String fileId) {
        return execute(api.retrieveFileContent(fileId));
    }

    public void retrieveFileContentAndSave(String fileId, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        retrieveFileContentAndSave(fileId, path);
    }

    public void retrieveFileContentAndSave(String fileId, Path filePath) throws IOException {
        ResponseBody responseBody = execute(api.retrieveFileContent(fileId));
        Path parentDir = filePath.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }

        try (BufferedSink sink = Okio.buffer(Okio.sink(filePath.toFile()))) {
            sink.writeAll(responseBody.source());
        }
    }

    public FileUrl retrieveFileUrl(String fileId) {
        return execute(api.retrieveFileUrl(fileId));
    }

    public FileUrl retrieveFileUrl(String fileId, Long expires) {
        return execute(api.retrieveFileUrl(fileId, expires));
    }

    public FileUrl retrievePreviewUrl(String fileId, Long expires) {
        return execute(api.retrievePreviewUrl(fileId, expires));
    }

    public ResponseBody retrieveDomTreeContent(String fileId) {
        return execute(api.retrieveDomTreeContent(fileId));
    }

    public FileUrl retrieveDomTreeUrl(String fileId, Long expires) {
        return execute(api.retrieveDomTreeUrl(fileId, expires));
    }

    public File uploadDomTree(String fileId, byte[] bytes, String filename) {
        RequestBody fileIdBody = RequestBody.create(MultipartBody.FORM, fileId);
        RequestBody fileBody = RequestBody.create(FileUtils.extraMediaType(filename), bytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", filename, fileBody);
        return execute(api.uploadDomTree(fileIdBody, body));
    }

    public File uploadDomTree(String fileId, Path filepath) {
        try (InputStream inputStream = Files.newInputStream(filepath)) {
            return uploadDomTree(fileId, inputStream, filepath.getFileName().toString());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload dom tree: " + filepath, e);
        }
    }

    public File uploadDomTree(String fileId, InputStream inputStream, String filename) {
        RequestBody fileIdBody = RequestBody.create(MultipartBody.FORM, fileId);
        RequestBody fileBody = createStreamRequestBody(inputStream, filename);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", filename, fileBody);
        return execute(api.uploadDomTree(fileIdBody, body));
    }

    public File uploadDomTreeJson(String fileId, Object domTree) {
        DomTreeJsonRequest request = DomTreeJsonRequest.builder()
                .fileId(fileId)
                .domTree(domTree)
                .build();
        return execute(api.uploadDomTreeJson(request));
    }

    public File uploadPdf(String fileId, byte[] bytes, String filename) {
        RequestBody fileIdBody = RequestBody.create(MultipartBody.FORM, fileId);
        RequestBody fileBody = RequestBody.create(FileUtils.extraMediaType(filename), bytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", filename, fileBody);
        return execute(api.uploadPdf(fileIdBody, body));
    }

    public File uploadPdf(String fileId, Path filepath) {
        try (InputStream inputStream = Files.newInputStream(filepath)) {
            return uploadPdf(fileId, inputStream, filepath.getFileName().toString());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload pdf: " + filepath, e);
        }
    }

    public File uploadPdf(String fileId, InputStream inputStream, String filename) {
        RequestBody fileIdBody = RequestBody.create(MultipartBody.FORM, fileId);
        RequestBody fileBody = createStreamRequestBody(inputStream, filename);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", filename, fileBody);
        return execute(api.uploadPdf(fileIdBody, body));
    }

    public Progress updateProgress(String fileId, String progressName, ProgressUpdateRequest request) {
        return execute(api.updateProgress(fileId, progressName, request));
    }

    public Progress retrieveProgress(String fileId, String progressName) {
        return execute(api.retrieveProgress(fileId, progressName));
    }

    public FileCropResponse cropImage(FileCropRequest request) {
        return execute(api.cropImage(request));
    }

    public File createDirectory(MkdirRequest request) {
        return execute(api.createDirectory(request));
    }

    public File createResource(CreateResourceRequest request) {
        return execute(api.createResource(request));
    }

    public List<File> findFiles(FindFilesQuery query) {
        Map<String, Object> queryMap = mapper.convertValue(query, new TypeReference<Map<String, Object>>() {});
        queryMap.values().removeIf(Objects::isNull);
        return execute(api.findFiles(queryMap)).data;
    }

    public File retrieveFileInfo(String fileId) {
        return execute(api.retrieveFileInfo(fileId));
    }

    public File updateFileDescription(String fileId, String description) {
        FileDescriptionUpdateRequest request = FileDescriptionUpdateRequest.builder()
                .description(description)
                .build();
        return execute(api.updateFileDescription(fileId, request));
    }

    public File updateFileCities(String fileId, List<String> cities) {
        FileCitiesUpdateRequest request = FileCitiesUpdateRequest.builder()
                .cities(cities)
                .build();
        return execute(api.updateFileCities(fileId, request));
    }

    public File updateFileTags(String fileId, List<String> tags) {
        FileTagsUpdateRequest request = FileTagsUpdateRequest.builder()
                .tags(tags)
                .build();
        return execute(api.updateFileTags(fileId, request));
    }

    public FileExistsResponse fileExists(FileExistsQuery query) {
        Map<String, Object> queryMap = mapper.convertValue(query, new TypeReference<Map<String, Object>>() {});
        queryMap.values().removeIf(Objects::isNull);
        return execute(api.fileExists(queryMap));
    }

    public File moveFile(FileMoveRequest request) {
        return execute(api.moveFile(request));
    }

    public FilePage pageFiles(PageFilesRequest request) {
        return execute(api.pageFiles(request));
    }

    public Map<String, List<String>> getFileAncestorIds(FileAncestorIdsRequest request) {
        return execute(api.getFileAncestorIds(request));
    }

    public FineTuningJob createFineTuningJob(FineTuningJobRequest request) {
        return execute(api.createFineTuningJob(request));
    }

    public List<FineTuningJob> listFineTuningJobs() {
        return execute(api.listFineTuningJobs()).data;
    }

    public FineTuningJob retrieveFineTuningJob(String fineTuningJobId) {
        return execute(api.retrieveFineTuningJob(fineTuningJobId));
    }

    public FineTuningJob cancelFineTuningJob(String fineTuningJobId) {
        return execute(api.cancelFineTuningJob(fineTuningJobId));
    }

    public List<FineTuningEvent> listFineTuningJobEvents(String fineTuningJobId) {
        return execute(api.listFineTuningJobEvents(fineTuningJobId)).data;
    }

    public List<FineTuningJobCheckpoint> listFineTuningCheckpoints(String fineTuningJobId) {
        return execute(api.listFineTuningCheckpoints(fineTuningJobId)).data;
    }

    @Deprecated
    public Flowable<CompletionChunk> streamCompletion(CompletionRequest request) {
        request.setStream(true);
        return stream(api.createCompletionStream(request), CompletionChunk.class);
    }

    /**
     * Upload a file using bytes.
     */
    public File uploadFile(String purpose, byte[] bytes, String filename) {
        RequestBody purposeBody = RequestBody.create(MultipartBody.FORM, purpose);
        RequestBody fileBody = RequestBody.create(FileUtils.extraMediaType(filename), bytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", filename, fileBody);
        return execute(api.uploadFile(purposeBody, body));
    }

    /**
     * Upload a file using file path.
     */
    public File uploadFile(String purpose, String filepath) {
        Path path = Paths.get(filepath);
        return uploadFile(purpose, path);
    }

    /**
     * Upload a file using file path.
     */
    public File uploadFile(String purpose, Path filepath) {

        try (InputStream inputStream = Files.newInputStream(filepath)) {
            return uploadFile(purpose, inputStream, filepath.getFileName().toString());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload file: " + filepath, e);
        }
    }

    /**
     * Upload a file using InputStream.
     */
    public File uploadFile(String purpose, InputStream fileInputStream, String filename) {
        RequestBody purposeBody = RequestBody.create(MultipartBody.FORM, purpose);
        RequestBody fileBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return FileUtils.extraMediaType(filename);
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try (Source source = Okio.source(fileInputStream)) {
                    sink.writeAll(source);
                }
            }
        };

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", filename, fileBody);
        return execute(api.uploadFile(purposeBody, body));
    }

    public File uploadFile(FileUploadRequest request, byte[] bytes, String filename) {
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", filename,
                RequestBody.create(FileUtils.extraMediaType(filename), bytes));
        return executeUploadFile(request, filePart);
    }

    public File uploadFile(FileUploadRequest request, Path filepath) {
        try (InputStream inputStream = Files.newInputStream(filepath)) {
            return uploadFile(request, inputStream, filepath.getFileName().toString());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload file: " + filepath, e);
        }
    }

    public File uploadFile(FileUploadRequest request, InputStream inputStream, String filename) {
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", filename,
                createStreamRequestBody(inputStream, filename));
        return executeUploadFile(request, filePart);
    }

    private File executeUploadFile(FileUploadRequest request, MultipartBody.Part filePart) {
        RequestBody purposeBody = request.getPurpose() != null
                ? RequestBody.create(MultipartBody.FORM, request.getPurpose()) : null;
        RequestBody metadataBody = request.getMetadata() != null
                ? RequestBody.create(MultipartBody.FORM, request.getMetadata()) : null;
        RequestBody getUrlBody = request.getGetUrl() != null
                ? RequestBody.create(MultipartBody.FORM, request.getGetUrl().toString()) : null;
        RequestBody expiresBody = request.getExpires() != null
                ? RequestBody.create(MultipartBody.FORM, request.getExpires().toString()) : null;
        RequestBody ancestorIdBody = request.getAncestorId() != null
                ? RequestBody.create(MultipartBody.FORM, request.getAncestorId()) : null;
        RequestBody overwriteBody = request.getOverwrite() != null
                ? RequestBody.create(MultipartBody.FORM, request.getOverwrite().toString()) : null;
        RequestBody descriptionBody = request.getDescription() != null
                ? RequestBody.create(MultipartBody.FORM, request.getDescription()) : null;
        RequestBody citiesBody = null;
        if (request.getCities() != null) {
            try {
                citiesBody = RequestBody.create(MultipartBody.FORM, mapper.writeValueAsString(request.getCities()));
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Failed to serialize cities", e);
            }
        }
        RequestBody tagsBody = null;
        if (request.getTags() != null) {
            try {
                tagsBody = RequestBody.create(MultipartBody.FORM, mapper.writeValueAsString(request.getTags()));
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Failed to serialize tags", e);
            }
        }
        return execute(api.uploadFile(purposeBody, filePart, metadataBody, getUrlBody, expiresBody,
                ancestorIdBody, overwriteBody, descriptionBody, citiesBody, tagsBody));
    }

    private RequestBody createStreamRequestBody(InputStream inputStream, String filename) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return FileUtils.extraMediaType(filename);
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try (Source source = Okio.source(inputStream)) {
                    sink.writeAll(source);
                }
            }
        };
    }

    public Batch createBatch(BatchRequest request) {
        return execute(api.createBatch(request));
    }

    public Batch retrieveBatch(String batchId) {
        return execute(api.retrieveBatch(batchId));
    }

    public static Flowable<AssistantSSE> assistantStream(Call<ResponseBody> apiCall) {
        return Flowable.create(emitter -> apiCall.enqueue(new AssistantResponseBodyCallback(emitter)), BackpressureStrategy.BUFFER);
    }


    public ImageResult createImage(CreateImageRequest request) {
        return execute(api.createImage(request));
    }

    public ImageResult createImageEdit(CreateImageEditRequest request, String imagePath, String maskPath) {
        java.io.File image = new java.io.File(imagePath);
        java.io.File mask = null;
        if (maskPath != null) {
            mask = new java.io.File(maskPath);
        }
        return createImageEdit(request, image, mask);
    }

    public ImageResult createImageEdit(CreateImageEditRequest request, java.io.File image, java.io.File mask) {
        RequestBody imageBody = RequestBody.create(MediaType.parse("image"), image);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MediaType.get("multipart/form-data"))
                .addFormDataPart("prompt", request.getPrompt())
                .addFormDataPart("size", request.getSize())
                .addFormDataPart("response_format", request.getResponseFormat())
                .addFormDataPart("image", "image", imageBody);

        if (request.getN() != null) {
            builder.addFormDataPart("n", request.getN().toString());
        }

        if (mask != null) {
            RequestBody maskBody = RequestBody.create(MediaType.parse("image"), mask);
            builder.addFormDataPart("mask", "mask", maskBody);
        }

        if (request.getModel() != null) {
            builder.addFormDataPart("model", request.getModel());
        }

        return execute(api.createImageEdit(builder.build()));
    }

    public ImageResult createImageVariation(CreateImageVariationRequest request, String imagePath) {
        java.io.File image = new java.io.File(imagePath);
        return createImageVariation(request, image);
    }

    public ImageResult createImageVariation(CreateImageVariationRequest request, java.io.File image) {
        RequestBody imageBody = RequestBody.create(MediaType.parse("image"), image);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MediaType.get("multipart/form-data"))
                .addFormDataPart("size", request.getSize())
                .addFormDataPart("response_format", request.getResponseFormat())
                .addFormDataPart("image", "image", imageBody);

        if (request.getN() != null) {
            builder.addFormDataPart("n", request.getN().toString());
        }

        if (request.getModel() != null) {
            builder.addFormDataPart("model", request.getModel());
        }

        return execute(api.createImageVariation(builder.build()));
    }

    public TranscriptionResult createTranscription(CreateTranscriptionRequest request, String audioPath) {
        java.io.File audio = new java.io.File(audioPath);
        return createTranscription(request, audio);
    }

    public TranscriptionResult createTranscription(CreateTranscriptionRequest request, java.io.File audio) {
        RequestBody audioBody = RequestBody.create(MediaType.parse("audio"), audio);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MediaType.get("multipart/form-data"))
                .addFormDataPart("model", request.getModel())
                .addFormDataPart("file", audio.getName(), audioBody);

        if (request.getPrompt() != null) {
            builder.addFormDataPart("prompt", request.getPrompt());
        }
        if (request.getResponseFormat() != null) {
            builder.addFormDataPart("response_format", request.getResponseFormat());
        }
        if (request.getTemperature() != null) {
            builder.addFormDataPart("temperature", request.getTemperature().toString());
        }
        if (request.getLanguage() != null) {
            builder.addFormDataPart("language", request.getLanguage());
        }
        if (request.getTimestampGranularities() != null && !request.getTimestampGranularities().isEmpty()) {
            for (String granularity : request.getTimestampGranularities()) {
                builder.addFormDataPart("timestamp_granularities[]", granularity);
            }
        }
        return execute(api.createTranscription(builder.build()));
    }

    public TranslationResult createTranslation(CreateTranslationRequest request, String audioPath) {
        java.io.File audio = new java.io.File(audioPath);
        return createTranslation(request, audio);
    }

    public TranslationResult createTranslation(CreateTranslationRequest request, java.io.File audio) {
        RequestBody audioBody = RequestBody.create(MediaType.parse("audio"), audio);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MediaType.get("multipart/form-data"))
                .addFormDataPart("model", request.getModel())
                .addFormDataPart("file", audio.getName(), audioBody);

        if (request.getPrompt() != null) {
            builder.addFormDataPart("prompt", request.getPrompt());
        }
        if (request.getResponseFormat() != null) {
            builder.addFormDataPart("response_format", request.getResponseFormat());
        }
        if (request.getTemperature() != null) {
            builder.addFormDataPart("temperature", request.getTemperature().toString());
        }

        return execute(api.createTranslation(builder.build()));
    }

    public ModerationResult createModeration(ModerationRequest request) {
        return execute(api.createModeration(request));
    }

    public ResponseBody createSpeech(CreateSpeechRequest request) {
        return execute(api.createSpeech(request));
    }

    public Assistant createAssistant(AssistantRequest request) {
        return execute(api.createAssistant(request));
    }

    public Assistant retrieveAssistant(String assistantId) {
        return execute(api.retrieveAssistant(assistantId));
    }

    public Assistant modifyAssistant(String assistantId, ModifyAssistantRequest request) {
        return execute(api.modifyAssistant(assistantId, request));
    }

    public DeleteResult deleteAssistant(String assistantId) {
        return execute(api.deleteAssistant(assistantId));
    }

    public OpenAiResponse<Assistant> listAssistants(ListSearchParameters params) {
        Map<String, Object> queryParameters = mapper.convertValue(params, new TypeReference<Map<String, Object>>() {
        });
        return execute(api.listAssistants(queryParameters));
    }

    public Thread createThread(ThreadRequest request) {
        return execute(api.createThread(request));
    }

    public Thread retrieveThread(String threadId) {
        return execute(api.retrieveThread(threadId));
    }

    public Thread modifyThread(String threadId, ThreadRequest request) {
        return execute(api.modifyThread(threadId, request));
    }

    public DeleteResult deleteThread(String threadId) {
        return execute(api.deleteThread(threadId));
    }

    public Message createMessage(String threadId, MessageRequest request) {
        return execute(api.createMessage(threadId, request));
    }

    public Message retrieveMessage(String threadId, String messageId) {
        return execute(api.retrieveMessage(threadId, messageId));
    }

    public Message modifyMessage(String threadId, String messageId, ModifyMessageRequest request) {
        return execute(api.modifyMessage(threadId, messageId, request));
    }

    public OpenAiResponse<Message> listMessages(String threadId, MessageListSearchParameters params) {
        Map<String, Object> queryParameters = mapper.convertValue(params, new TypeReference<Map<String, Object>>() {
        });
        return execute(api.listMessages(threadId, queryParameters));
    }

    public DeleteResult deleteMessage(String threadId, String messageId) {
        return execute(api.deleteMessage(threadId, messageId));
    }


    public Run createRun(String threadId, RunCreateRequest runCreateRequest) {
        return execute(api.createRun(threadId, runCreateRequest));
    }

    public OpenAiResponse<Batch> listBatches(ListSearchParameters params) {
        Map<String, Object> queryParameters = mapper.convertValue(params, new TypeReference<Map<String, Object>>() {
        });
        return execute(api.listBatches(queryParameters));
    }


    public Run retrieveRun(String threadId, String runId) {
        return execute(api.retrieveRun(threadId, runId));
    }

    public Run modifyRun(String threadId, String runId, ModifyRunRequest request) {
        return execute(api.modifyRun(threadId, runId, request));
    }

    public OpenAiResponse<Run> listRuns(String threadId, ListSearchParameters listSearchParameters) {
        Map<String, String> search = new HashMap<>();
        if (listSearchParameters != null) {
            search = mapper.convertValue(listSearchParameters, Map.class);
        }
        return execute(api.listRuns(threadId, search));
    }

    public Run submitToolOutputs(String threadId, String runId, SubmitToolOutputsRequest submitToolOutputsRequest) {
        return execute(api.submitToolOutputs(threadId, runId, submitToolOutputsRequest));
    }

    public Flowable<AssistantSSE> createRunStream(String threadId, RunCreateRequest runCreateRequest) {
        runCreateRequest.setStream(true);
        return assistantStream(api.createRunStream(threadId, runCreateRequest));
    }


    public Run cancelRun(String threadId, String runId) {
        return execute(api.cancelRun(threadId, runId));
    }

    public Run createThreadAndRun(CreateThreadAndRunRequest createThreadAndRunRequest) {
        return execute(api.createThreadAndRun(createThreadAndRunRequest));
    }

    public Flowable<AssistantSSE> createThreadAndRunStream(CreateThreadAndRunRequest createThreadAndRunRequest) {
        createThreadAndRunRequest.setStream(true);
        return assistantStream(api.createThreadAndRunStream(createThreadAndRunRequest));
    }


    public RunStep retrieveRunStep(String threadId, String runId, String stepId) {
        return execute(api.retrieveRunStep(threadId, runId, stepId));
    }

    public OpenAiResponse<RunStep> listRunSteps(String threadId, String runId, ListSearchParameters listSearchParameters) {
        Map<String, String> search = new HashMap<>();
        if (listSearchParameters != null) {
            search = mapper.convertValue(listSearchParameters, Map.class);
        }
        return execute(api.listRunSteps(threadId, runId, search));
    }


    public VectorStore createVectorStore(VectorStoreRequest request) {
        return execute(api.createVectorStore(request));
    }

    public OpenAiResponse<VectorStore> listVectorStores(ListSearchParameters listSearchParameters) {
        Map<String, Object> search = new HashMap<>();
        if (listSearchParameters != null) {
            search = mapper.convertValue(listSearchParameters, Map.class);
        }
        return execute(api.listVectorStores(search));
    }

    public VectorStore retrieveVectorStore(String vectorStoreId) {
        return execute(api.retrieveVectorStore(vectorStoreId));
    }

    public VectorStore modifyVectorStore(String vectorStoreId, ModifyVectorStoreRequest request) {
        return execute(api.modifyVectorStore(vectorStoreId, request));
    }

    public DeleteResult deleteVectorStore(String vectorStoreId) {
        return execute(api.deleteVectorStore(vectorStoreId));
    }

    public VectorStoreFile createVectorStoreFile(String vectorStoreId, VectorStoreFileRequest fileRequest) {
        return execute(api.createVectorStoreFile(vectorStoreId, fileRequest));
    }

    public OpenAiResponse<VectorStoreFile> listVectorStoreFiles(String vectorStoreId, ListSearchParameters listSearchParameters) {
        Map<String, Object> search = new HashMap<>();
        if (listSearchParameters != null) {
            search = mapper.convertValue(listSearchParameters, Map.class);
        }
        return execute(api.listVectorStoreFiles(vectorStoreId, search));
    }

    public VectorStoreFile retrieveVectorStoreFile(String vectorStoreId, String fileId) {
        return execute(api.retrieveVectorStoreFile(vectorStoreId, fileId));
    }

    public DeleteResult deleteVectorStoreFile(String vectorStoreId, String fileId) {
        return execute(api.deleteVectorStoreFile(vectorStoreId, fileId));
    }

    public VectorStoreFilesBatch createVectorStoreFileBatch(String vectorStoreId, VectorStoreFilesBatchRequest request) {
        return execute(api.createVectorStoreFileBatch(vectorStoreId, request));
    }

    public VectorStoreFilesBatch retrieveVectorStoreFileBatch(String vectorStoreId, String batchId) {
        return execute(api.retrieveVectorStoreFileBatch(vectorStoreId, batchId));
    }

    public VectorStoreFilesBatch cancelVectorStoreFileBatch(String vectorStoreId, String batchId) {
        return execute(api.cancelVectorStoreFileBatch(vectorStoreId, batchId));
    }

    public OpenAiResponse<VectorStoreFile> listVectorStoreFilesInBatch(String vectorStoreId, String batchId, ListSearchParameters listSearchParameters) {
        Map<String, Object> search = new HashMap<>();
        if (listSearchParameters != null) {
            search = mapper.convertValue(listSearchParameters, Map.class);
        }
        return execute(api.listVectorStoreFilesInBatch(vectorStoreId, batchId, search));
    }

    public Flowable<AssistantSSE> submitToolOutputsStream(String threadId, String runId, SubmitToolOutputsRequest submitToolOutputsRequest) {
        submitToolOutputsRequest.setStream(true);
        return assistantStream(api.submitToolOutputsStream(threadId, runId, submitToolOutputsRequest));
    }

    public WebSearchResponse webSearch(WebSearchRequest webSearchRequest) {
        return execute(api.webSearch(webSearchRequest));
    }

    public WebCrawlResponse webCrawl(WebCrawlRequest webCrawlRequest) {
        return execute(api.webCrawl(webCrawlRequest));
    }

    public WebExtractResponse webExtract(WebExtractRequest webExtractRequest) {
        return execute(api.webExtract(webExtractRequest));
    }

    // Response API operations

    /**
     * Create a response (non-streaming mode).
     *
     * @param request The create response request
     * @return The created response object
     */
    public com.theokanning.openai.response.Response createResponse(com.theokanning.openai.response.CreateResponseRequest request) {
        return execute(api.createResponse(request));
    }

    /**
     * Create a response with streaming (SSE protocol).
     * The stream parameter in the request will be automatically set to true.
     *
     * @param request The create response request
     * @return A Flowable of ResponseSSE events
     */
    public Flowable<ResponseSSE> createResponseStream(com.theokanning.openai.response.CreateResponseRequest request) {
        request.setStream(true);
        return responseStream(api.createResponseStream(request));
    }

    /**
     * Retrieve a response by ID.
     *
     * @param responseId The response ID
     * @return The response object
     */
    public com.theokanning.openai.response.Response getResponse(String responseId) {
        return execute(api.getResponse(responseId));
    }

    /**
     * Helper method to create a Flowable from Response API SSE stream.
     *
     * @param apiCall The Retrofit call for streaming
     * @return A Flowable of ResponseSSE events
     */
    public static Flowable<ResponseSSE> responseStream(Call<ResponseBody> apiCall) {
        return Flowable.create(emitter -> apiCall.enqueue(new ResponseResponseBodyCallback(emitter)), BackpressureStrategy.BUFFER);
    }

    /**
     * Account information inquiry: including total amount and other information.
     *
     * @return Account information.
     */
    public Subscription subscription() {
        Single<Subscription> subscription = api.subscription();
        return subscription.blockingGet();
    }

    // Space operations

    private static boolean bellaSuccess(com.theokanning.openai.BellaResponse<Boolean> resp) {
        if (resp.getData() != null) {
            return resp.getData();
        }
        return resp.getCode() == 200;
    }

    public String createSpace(com.theokanning.openai.space.CreateSpaceRequest request) {
        return execute(api.createSpace(request)).getData();
    }

    public com.theokanning.openai.BellaResponse<String> createSpaceRaw(com.theokanning.openai.space.CreateSpaceRequest request) {
        return execute(api.createSpace(request));
    }

    public boolean updateSpaceName(com.theokanning.openai.space.UpdateSpaceNameRequest request) {
        return bellaSuccess(execute(api.updateSpaceName(request)));
    }

    public com.theokanning.openai.space.Space getSpace(String spaceCode) {
        return execute(api.getSpace(spaceCode)).getData();
    }

    public java.util.List<com.theokanning.openai.space.Space> listSpaces(java.util.List<String> spaceCodes) {
        return execute(api.listSpaces(spaceCodes)).getData();
    }

    public boolean changeSpaceOwner(com.theokanning.openai.space.ChangeSpaceOwnerRequest request) {
        return bellaSuccess(execute(api.changeSpaceOwner(request)));
    }

    public boolean createRole(com.theokanning.openai.space.CreateRoleRequest request) {
        return bellaSuccess(execute(api.createRole(request)));
    }

    public java.util.List<com.theokanning.openai.space.RoleWithSpace> listMemberRoles(String memberUid) {
        return execute(api.listMemberRoles(memberUid)).getData();
    }

    public boolean createMembers(com.theokanning.openai.space.CreateMemberRequest request) {
        return bellaSuccess(execute(api.createMembers(request)));
    }

    public boolean removeMember(com.theokanning.openai.space.RemoveMemberRequest request) {
        return bellaSuccess(execute(api.removeMember(request)));
    }

    public boolean updateMemberRole(com.theokanning.openai.space.UpdateMemberRoleRequest request) {
        return bellaSuccess(execute(api.updateMemberRole(request)));
    }

    public boolean exitSpace(com.theokanning.openai.space.ExitSpaceRequest request) {
        return bellaSuccess(execute(api.exitSpace(request)));
    }

    public java.util.List<com.theokanning.openai.space.Member> listMembers(String spaceCode) {
        return execute(api.listMembers(spaceCode)).getData();
    }

    public com.theokanning.openai.space.RoleWithSpace getMemberRole(String memberUid, String spaceCode) {
        return execute(api.getMemberRole(memberUid, spaceCode)).getData();
    }

    /**
     * Calls the Open AI api, returns the response, and parses error messages if the request fails
     */
    public static <T> T execute(Single<T> apiCall) {
        try {
            return apiCall.blockingGet();
        } catch (HttpException e) {
            try {
                if (e.response() == null || e.response().errorBody() == null) {
                    throw e;
                }
                String errorBody = e.response().errorBody().string();

                OpenAiError error = mapper.readValue(errorBody, OpenAiError.class);
                throw new OpenAiHttpException(error, e, e.code());
            } catch (IOException ex) {
                // couldn't parse OpenAI error
                throw e;
            }
        }
    }

    /**
     * Calls the Open AI api and returns a Flowable of SSE for streaming
     * omitting the last message.
     *
     * @param apiCall The api call
     */
    public static Flowable<SSE> stream(Call<ResponseBody> apiCall) {
        return stream(apiCall, false);
    }

    /**
     * Account API consumption amount information inquiry.
     * Up to 100 days of inquiry.
     *
     * @param starDate
     * @param endDate
     * @return Consumption amount information.
     */
    public BillingUsage billingUsage(@NotNull LocalDate starDate, @NotNull LocalDate endDate) {
        Single<BillingUsage> billingUsage = api.billingUsage(starDate, endDate);
        return billingUsage.blockingGet();
    }


    /**
     * Calls the Open AI api and returns a Flowable of SSE for streaming.
     *
     * @param apiCall  The api call
     * @param emitDone If true the last message ([DONE]) is emitted
     */
    public static Flowable<SSE> stream(Call<ResponseBody> apiCall, boolean emitDone) {
        return Flowable.create(emitter -> apiCall.enqueue(new ResponseBodyCallback(emitter, emitDone)), BackpressureStrategy.BUFFER);
    }

    /**
     * Calls the Open AI api and returns a Flowable of type T for streaming
     * omitting the last message.
     *
     * @param apiCall The api call
     * @param cl      Class of type T to return
     */
    public static <T> Flowable<T> stream(Call<ResponseBody> apiCall, Class<T> cl) {
        return stream(apiCall).map(sse -> mapper.readValue(sse.getData(), cl));
    }

    /**
     * Calls the Open AI api and returns a Flowable of type T for streaming
     * omitting the last message.
     * @param apiCall The api call
     * @param cl Class of type T to return
     * @param consumer After the instance creation is complete
     * @param newInstance If the serialization fails, call this interface to get an instance
     */
    public static <T> Flowable<T> stream(Call<ResponseBody> apiCall, Class<T> cl, BiConsumer<T, SSE> consumer,
                                         Supplier<T> newInstance) {
        return stream(apiCall, true).map(sse -> {
            try {
                T t = mapper.readValue(sse.getData(), cl);
                if (Objects.nonNull(consumer)) {
                    consumer.accept(t, sse);
                }
                return t;
            } catch (JsonProcessingException e) {
                T t = newInstance.get();
                consumer.accept(t, sse);
                return t;
            }
        });
    }

    /**
     * Shuts down the OkHttp ExecutorService.
     * The default behaviour of OkHttp's ExecutorService (ConnectionPool)
     * is to shut down after an idle timeout of 60s.
     * Call this method to shut down the ExecutorService immediately.
     */
    public void shutdownExecutor() {
        Objects.requireNonNull(this.executorService, "executorService must be set in order to shut down");
        this.executorService.shutdown();
    }

    public Batch cancelBatch(String batchId) {
        return execute(api.cancelBatch(batchId));
    }

    // Queue operations
    public String registerQueue(Register register) {
        return execute(api.registerQueue(register));
    }

    public EventbusConfig getEventbus() {
        return execute(api.getEventbus());
    }

    public Object putTask(Put put) {
        return execute(api.putTask(put));
    }

    public Map<String, List<Task>> takeTasks(Take take) {
        return execute(api.takeTasks(take));
    }

    public String cancelTask(String taskId) {
        return execute(api.cancelTask(taskId));
    }

    public String completeTask(String taskId, Map<String, Object> data) {
        return execute(api.completeTask(taskId, data));
    }

    public Task getTask(String taskId) {
        return execute(api.getTask(taskId));
    }

    public Queue getQueue(String queue) {
        return execute(api.getQueue(queue));
    }

    public static OpenAiApi buildApi(String token, Duration timeout, String baseUrl) {
        OkHttpClient client = defaultClient(token, timeout);
        Retrofit retrofit = defaultRetrofit(client, mapper, baseUrl);

        return retrofit.create(OpenAiApi.class);
    }


    public static OkHttpClient defaultClient(String token, Duration timeout) {
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(token))
                .connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS))
                .readTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .build();
    }

    public static Retrofit defaultRetrofit(OkHttpClient client, ObjectMapper mapper, String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 将流映射到累加器。支持content/function call/tool call的累加
     *
     * @param flowable 流对象
     * @return 累加器流
     */
    public Flowable<ChatMessageAccumulator> mapStreamToAccumulator(Flowable<ChatCompletionChunk> flowable) {
        ChatFunctionCall functionCall = new ChatFunctionCall(null, null);
        AssistantMessage accumulatedMessage = new AssistantMessage();
        return flowable.map(chunk -> {
            List<ChatCompletionChoice> choices = chunk.getChoices();
            AssistantMessage messageChunk=new AssistantMessage();
            if (choices!=null && !choices.isEmpty()){
                ChatCompletionChoice firstChoice = choices.get(0);
                messageChunk = firstChoice.getMessage();
                appendContent(messageChunk, accumulatedMessage);
                processFunctionCall(messageChunk, functionCall, accumulatedMessage);
                processToolCalls(messageChunk, accumulatedMessage);
                if (firstChoice.getFinishReason() != null) {
                    handleFinishReason(firstChoice.getFinishReason(), functionCall, accumulatedMessage);
                }
            }
            return new ChatMessageAccumulator(messageChunk, accumulatedMessage,chunk.getUsage());
        });
    }

    public Flowable<ChatMessageAccumulatorWrapper> mapStreamToAccumulatorWrapper(Flowable<ChatCompletionChunk> flowable) {
        ChatFunctionCall functionCall = new ChatFunctionCall(null, null);
        AssistantMessage accumulatedMessage = new AssistantMessage();
        return flowable.map(chunk -> {
            List<ChatCompletionChoice> choices = chunk.getChoices();
            AssistantMessage messageChunk = null;
            if (null != choices && !choices.isEmpty()) {
                ChatCompletionChoice firstChoice = choices.get(0);
                messageChunk = firstChoice.getMessage();
                appendContent(messageChunk, accumulatedMessage);
                processFunctionCall(messageChunk, functionCall, accumulatedMessage);
                processToolCalls(messageChunk, accumulatedMessage);
                if (firstChoice.getFinishReason() != null) {
                    handleFinishReason(firstChoice.getFinishReason(), functionCall, accumulatedMessage);
                }
            }
            ChatMessageAccumulator chatMessageAccumulator = new ChatMessageAccumulator(messageChunk, accumulatedMessage, chunk.getUsage());
            return new ChatMessageAccumulatorWrapper(chatMessageAccumulator, chunk);
        });
    }

    /**
     * 处理消息块中的函数调用。
     *
     * @param messageChunk       消息块
     * @param functionCall       函数调用对象
     * @param accumulatedMessage 累加的消息对象
     */
    private void processFunctionCall(AssistantMessage messageChunk, ChatFunctionCall functionCall, AssistantMessage accumulatedMessage) {
        Optional.ofNullable(messageChunk.getFunctionCall())
                .ifPresent(messageFunctionCall -> {
                    updateFunctionCall(messageFunctionCall, functionCall);
                    accumulatedMessage.setFunctionCall(functionCall);
                });
    }

    /**
     * 更新函数调用对象。
     *
     * @param messageFunctionCall 消息中的函数调用对象
     * @param functionCall        要更新的函数调用对象
     */
    private void updateFunctionCall(ChatFunctionCall messageFunctionCall, ChatFunctionCall functionCall) {
        Optional.ofNullable(messageFunctionCall.getName()).ifPresent(name ->
                functionCall.setName((functionCall.getName() == null ? "" : functionCall.getName()) + name)
        );

        Optional.ofNullable(messageFunctionCall.getArguments()).ifPresent(argNode -> {
            if (argNode instanceof ObjectNode) {
                functionCall.setArguments(argNode);
            } else if (argNode instanceof TextNode) {
                String argumentsPart = argNode.asText();
                functionCall.setArguments(new TextNode((functionCall.getArguments() == null ? "" : functionCall.getArguments().asText()) + argumentsPart));
            }
        });
    }

    /**
     * 处理消息块中的工具调用。
     *
     * @param messageChunk       消息块
     * @param accumulatedMessage 累加的消息对象
     */
    private void processToolCalls(AssistantMessage messageChunk, AssistantMessage accumulatedMessage) {
        Optional.ofNullable(messageChunk.getToolCalls()).ifPresent(toolCalls -> {
            ChatToolCall partToolCall = toolCalls.get(0);
            ChatFunctionCall partFunction = partToolCall.getFunction();
            int index = partToolCall.getIndex();
            List<ChatToolCall> accumulatedChatTools = getOrInitializeToolCalls(accumulatedMessage);

            ChatToolCall accumulatedToolCall = accumulatedChatTools.stream()
                    .filter(chatToolCall -> chatToolCall.getIndex() == index)
                    .findFirst()
                    .orElseGet(() -> {
                        ChatToolCall newToolCall = new ChatToolCall(index, partToolCall.getId(), partToolCall.getType());
                        accumulatedChatTools.add(newToolCall);
                        return newToolCall;
                    });

            updateFunctionCall(partFunction, accumulatedToolCall.getFunction());
        });
    }

    /**
     * 获取或初始化工具调用列表。
     *
     * @param accumulatedMessage 累加的消息对象
     * @return 工具调用列表
     */
    private List<ChatToolCall> getOrInitializeToolCalls(AssistantMessage accumulatedMessage) {
        return Optional.ofNullable(accumulatedMessage.getToolCalls()).orElseGet(() -> {
            List<ChatToolCall> newToolCalls = new ArrayList<>();
            accumulatedMessage.setToolCalls(newToolCalls);
            return newToolCalls;
        });
    }

    /**
     * 追加消息内容。
     *
     * @param messageChunk       消息块
     * @param accumulatedMessage 累加的消息对象
     */
    private void appendContent(AssistantMessage messageChunk, AssistantMessage accumulatedMessage) {
        accumulatedMessage.setContent(Optional.ofNullable(accumulatedMessage.getContent()).orElse("") +
                Optional.ofNullable(messageChunk.getContent()).orElse(""));
    }

    /**
     * 处理最后的完成
     *
     * @param finishReason       完成原因
     * @param functionCall       函数调用对象
     * @param accumulatedMessage 累加的消息对象
     * @throws IOException 可能抛出的IO异常
     */
    private void handleFinishReason(String finishReason, ChatFunctionCall functionCall, AssistantMessage accumulatedMessage) throws IOException {
        if ("function_call".equals(finishReason) && functionCall.getArguments() instanceof TextNode) {
            functionCall.setArguments(mapper.readTree(functionCall.getArguments().asText()));
            accumulatedMessage.setFunctionCall(functionCall);
        }
        if ("tool_calls".equals(finishReason)) {
            List<ChatToolCall> toolCalls = accumulatedMessage.getToolCalls();
            if (toolCalls != null) {
                toolCalls.sort(Comparator.comparingInt(ChatToolCall::getIndex));
                for (ChatToolCall chatToolCall : toolCalls) {
                    if (chatToolCall.getFunction().getArguments() instanceof TextNode) {
                        chatToolCall.getFunction().setArguments(mapper.readTree(chatToolCall.getFunction().getArguments().asText()));
                    }
                }
            }
        }
    }

}
