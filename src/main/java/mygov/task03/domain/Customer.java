package mygov.task03.domain;

import java.time.Instant;
import java.util.UUID;

public record Customer(
        UUID id,
        UUID companyId,
        String name,
        String email,
        String phone,
        Instant createdAt
) {}
