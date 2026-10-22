package mygov.task03.repository;

import mygov.task03.model.AuditLog;

import java.util.UUID;

public interface AuditRepository {

    void insert(AuditLog auditLog);

    void findByEntityId(UUID entityId);
}
