package com.theokanning.openai.completion.chat;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * A chat completion generated by OpenAI
 */
@Data
public class ChatCompletionChoice {

    /**
     * This index of this completion in the returned list.
     */
    Integer index;

    /**
     * The {@link ChatMessageRole#ASSISTANT} message or delta (when streaming) which was generated
     */
    @JsonAlias("delta")
    ChatMessage message;

    /**
     * The reason the model stopped generating tokens.
     * stop: This will be stop if the model hit a natural stop point or a provided stop sequence;
     * length:  if the maximum number of tokens specified in the request was reached;
     * content_filter: if content was omitted due to a flag from our content filters;
     * tool_calls : if the model called a tool;
     * function_call : (deprecated) if the model called a function.
     */
    @JsonProperty("finish_reason")
    String finishReason;

    /**
     * Log probability information for the choice.
     */
    Logprobs logprobs;


}
