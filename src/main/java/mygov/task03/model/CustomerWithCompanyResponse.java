package mygov.task03.model;

import java.time.Instant;
import java.util.UUID;

public record CustomerWithCompanyResponse(
        UUID id,
        String name,
        String email,
        String phone,
        CompanySummary company,
        Instant createdAt
) {}
