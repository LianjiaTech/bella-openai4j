package com.theokanning.openai.response;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status values for response items.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
public enum ItemStatus {

    /**
     * Item is currently being processed.
     */
    IN_PROGRESS("in_progress"),

    /**
     * Item has been completed successfully.
     */
    COMPLETED("completed"),

    /**
     * Item was incomplete due to constraints.
     */
    INCOMPLETE("incomplete");

    private final String value;

    ItemStatus(String value) {
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
