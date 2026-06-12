package org.acme.assistant.guard;

import java.time.Duration;

import org.acme.approval.ApprovalService;

import io.quarkiverse.langchain4j.guardrails.ToolInputGuardrail;
import io.quarkiverse.langchain4j.guardrails.ToolInputGuardrailRequest;
import io.quarkiverse.langchain4j.guardrails.ToolInputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class HumanApproval implements ToolInputGuardrail {

    private static final Duration APPROVAL_TIMEOUT = Duration.ofMinutes(2);

    @Inject
    ApprovalService approvalService;

    @Override
    public ToolInputGuardrailResult validate(ToolInputGuardrailRequest request) {
        approvalService.createApprovalRequest(request.toolName(), request.arguments());
        boolean approved = approvalService.awaitDecision(request.toolName(), APPROVAL_TIMEOUT);
        if (approved) {
            return ToolInputGuardrailResult.success();
        }
        return ToolInputGuardrailResult.failure(
                "The user rejected the '" + request.toolName() + "' action (or did not respond in time). "
                        + "Do NOT retry the tool. Inform the user the action was not performed.");
    }
}
