package com.theokanning.openai.response;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status values for responses in the Response API.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
public enum ResponseStatus {

    /**
     * Response has been completed successfully.
     */
    COMPLETED("completed"),

    /**
     * Response generation failed.
     */
    FAILED("failed"),

    /**
     * Response is currently being generated.
     */
    IN_PROGRESS("in_progress"),

    /**
     * Response was cancelled.
     */
    CANCELLED("cancelled"),

    /**
     * Response is queued for processing.
     */
    QUEUED("queued"),

    /**
     * Response was incomplete due to constraints.
     */
    INCOMPLETE("incomplete");

    private final String value;

    ResponseStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
