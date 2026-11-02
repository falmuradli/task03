package mygov.task03.repository;

import mygov.task03.domain.AuditLog;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public class JdbcAuditRepository implements AuditRepository {

    private final JdbcClient jdbcClient;

    public JdbcAuditRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void insert(AuditLog auditLog) {
         jdbcClient
                .sql("""
                        INSERT INTO audit_logs(id, entityId, action, details, createdAt)
                        VALUES (:id, :entitId, :action, :details, :createdAt)""")
                .param("id", auditLog.id())
                .param("entityId", auditLog.entityId())
                .param("action", auditLog.action())
                .param("details", auditLog.details())
                .param("createdAt", Timestamp.from(auditLog.createdAt()))
                 .update();
    }

    @Override
    public List<AuditLog> findByEntityId(UUID entityId) {
        return jdbcClient
                .sql("""
                        SELECT id, entity_id, action, details, created_at FROM audit_logs WHERE entity_id = :entityId
                        ORDER BY created_at, id""")
                .param("entityId", entityId)
                .query(AuditLog.class)
                .list();
    }
}
