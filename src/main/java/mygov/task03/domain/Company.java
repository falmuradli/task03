package mygov.task03.domain;

import java.time.Instant;
import java.util.UUID;

public record Company(
        UUID id,
        String name,
        String taxNumber,
        Instant createdAt
) {}
