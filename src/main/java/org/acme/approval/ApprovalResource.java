package org.acme.approval;

import java.util.Map;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/approvals")
@Produces(MediaType.APPLICATION_JSON)
public class ApprovalResource {

    public record ApprovalView(String status, String arguments) {
    }

    @Inject
    ApprovalService approvalService;

    // Shape expected by the frontend: { "<toolName>": { "status": "PENDING", "arguments": "{...}" } }
    @GET
    public Map<String, ApprovalView> pending() {
        return approvalService.pendingRequests().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> new ApprovalView("PENDING", e.getValue())));
    }

    @POST
    @Path("/{toolName}/approve")
    public void approve(@PathParam("toolName") String toolName) {
        approvalService.approve(toolName);
    }

    @POST
    @Path("/{toolName}/reject")
    public void reject(@PathParam("toolName") String toolName) {
        approvalService.reject(toolName);
    }
}
