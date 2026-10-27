package mygov.task03.domain;

import java.util.UUID;

public record CompanySummary(
        UUID id,
        String name,
        String taxNumber
) {}
