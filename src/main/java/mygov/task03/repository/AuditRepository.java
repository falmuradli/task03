package mygov.task03.repository;

import mygov.task03.domain.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditRepository {

    void insert(AuditLog auditLog);

    List<AuditLog> findByEntityId(UUID entityId);
}
