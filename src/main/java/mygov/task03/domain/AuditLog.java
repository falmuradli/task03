package mygov.task03.domain;

import java.time.Instant;
import java.util.UUID;

public record AuditLog(
        UUID id,
        UUID entityId,
        String action,
        String details,
        Instant createdAt
) {}
