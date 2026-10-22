package mygov.task03.model;

import java.time.Instant;
import java.util.UUID;

public record AuditLog(
        UUID id,
        UUID entityId,
        String action,
        String details,
        Instant createdAt
) {}
