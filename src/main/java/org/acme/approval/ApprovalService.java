package org.acme.approval;

import java.time.Duration;
import java.util.Map;

public interface ApprovalService {

    void createApprovalRequest(String toolName, String arguments);

    /**
     * Blocks until the user approves or rejects the request, or the timeout elapses.
     *
     * @return true if approved, false if rejected or timed out
     */
    boolean awaitDecision(String toolName, Duration timeout);

    void approve(String toolName);

    void reject(String toolName);

    /**
     * @return pending requests, keyed by tool name, value = tool arguments (JSON string)
     */
    Map<String, String> pendingRequests();
}
