package mygov.task03.model;

import java.time.Instant;
import java.util.UUID;

public record Company(
        UUID id,
        String name,
        String taxNumber,
        Instant createdAt
) {}
