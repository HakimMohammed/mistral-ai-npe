package org.acme.approval;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InMemoryApprovalService implements ApprovalService {

    private static final Logger LOG = Logger.getLogger(InMemoryApprovalService.class);

    private record PendingApproval(String arguments, CompletableFuture<Boolean> decision) {
    }

    private final Map<String, PendingApproval> requests = new ConcurrentHashMap<>();

    @Override
    public void createApprovalRequest(String toolName, String arguments) {
        requests.computeIfAbsent(toolName, k -> {
            LOG.infof("Approval REQUESTED for tool '%s' with arguments %s", toolName, arguments);
            return new PendingApproval(arguments, new CompletableFuture<>());
        });
    }

    @Override
    public boolean awaitDecision(String toolName, Duration timeout) {
        PendingApproval pending = requests.computeIfAbsent(toolName,
                k -> new PendingApproval(null, new CompletableFuture<>()));
        try {
            return pending.decision().get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            LOG.infof("Approval TIMED OUT for tool '%s'", toolName);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            return false;
        } finally {
            requests.remove(toolName);
        }
    }

    @Override
    public void approve(String toolName) {
        LOG.infof("Approval GRANTED for tool '%s'", toolName);
        complete(toolName, true);
    }

    @Override
    public void reject(String toolName) {
        LOG.infof("Approval REJECTED for tool '%s'", toolName);
        complete(toolName, false);
    }

    @Override
    public Map<String, String> pendingRequests() {
        return requests.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        // Collectors.toMap rejects null values
                        e -> e.getValue().arguments() == null ? "{}" : e.getValue().arguments()));
    }

    private void complete(String toolName, boolean approved) {
        PendingApproval pending = requests.get(toolName);
        if (pending == null) {
            LOG.warnf("No pending approval request for tool '%s'", toolName);
            return;
        }
        pending.decision().complete(approved);
    }
}
