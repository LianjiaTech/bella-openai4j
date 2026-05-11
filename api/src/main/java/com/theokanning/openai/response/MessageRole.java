package com.theokanning.openai.response;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Message roles in the Response API. Defines the hierarchy of instructions based on role.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
public enum MessageRole {

    /**
     * User role for input messages.
     */
    USER("user"),

    /**
     * Assistant role for output messages.
     */
    ASSISTANT("assistant"),

    /**
     * System role for system instructions.
     */
    SYSTEM("system"),

    /**
     * Developer role for developer instructions.
     */
    DEVELOPER("developer");

    private final String value;

    MessageRole(String value) {
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
