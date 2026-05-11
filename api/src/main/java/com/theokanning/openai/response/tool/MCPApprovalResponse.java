package com.theokanning.openai.response.tool;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.response.ConversationItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A response to an MCP approval request.
 *
 * @see <a href="https://developers.openai.com/api/reference/resources/responses/methods/create">Response API</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MCPApprovalResponse implements ConversationItem {

    /**
     * The type of the item. Always "mcp_approval_response".
     */
    @Builder.Default
    private String type = "mcp_approval_response";

    /**
     * The unique ID of the approval response.
     */
    private String id;

    /**
     * The ID of the approval request being answered.
     */
    @JsonProperty("approval_request_id")
    private String approvalRequestId;

    /**
     * Whether the request was approved.
     */
    private Boolean approve;

    /**
     * Optional reason for the decision.
     */
    private String reason;

    @Override
    public String getType() {
        return type;
    }
}
