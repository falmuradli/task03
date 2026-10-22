package mygov.task03.model;

import java.util.UUID;

public record CompanySummary(
        UUID id,
        String name,
        String taxNumber
) {}
