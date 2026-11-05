package mygov.task03;

import mygov.task03.domain.AuditLog;
import mygov.task03.repository.JdbcAuditRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcAuditRepositoryTest {

    @Mock
    private JdbcClient jdbcClient;

    @Mock
    private JdbcClient.StatementSpec statementSpec;

    @Mock
    private JdbcClient.MappedQuerySpec<AuditLog> auditQuerySpec;

    @InjectMocks
    private JdbcAuditRepository jdbcAuditRepository;

    @Test
    void insertBindsAuditFieldsAndExecutesUpdate() {
        AuditLog auditLog = sampleAuditLog();
        Timestamp createdAt = Timestamp.from(auditLog.createdAt());

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("id", auditLog.id())).thenReturn(statementSpec);
        when(statementSpec.param("entityId", auditLog.entityId())).thenReturn(statementSpec);
        when(statementSpec.param("action", auditLog.action())).thenReturn(statementSpec);
        when(statementSpec.param("details", auditLog.details())).thenReturn(statementSpec);
        when(statementSpec.param("createdAt", createdAt)).thenReturn(statementSpec);
        when(statementSpec.update()).thenReturn(1);

        jdbcAuditRepository.insert(auditLog);

        verify(statementSpec).param("id", auditLog.id());
        verify(statementSpec).param("entityId", auditLog.entityId());
        verify(statementSpec).param("action", auditLog.action());
        verify(statementSpec).param("details", auditLog.details());
        verify(statementSpec).param("createdAt", createdAt);
        verify(statementSpec).update();
    }

    @Test
    void findByEntityIdReturnsMatchingAuditLogs() {
        AuditLog auditLog = sampleAuditLog();
        UUID entityId = auditLog.entityId();
        List<AuditLog> expected = List.of(auditLog);

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("entityId", entityId)).thenReturn(statementSpec);
        when(statementSpec.query(AuditLog.class)).thenReturn(auditQuerySpec);
        when(auditQuerySpec.list()).thenReturn(expected);

        List<AuditLog> result = jdbcAuditRepository.findByEntityId(entityId);

        assertEquals(expected, result);
        verify(statementSpec).param("entityId", entityId);
    }

    private static AuditLog sampleAuditLog() {
        return new AuditLog(
                UUID.fromString("33333333-3333-4333-8333-333333333333"),
                UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e"),
                "COMPANY_REGISTERED",
                "Registered Company_01",
                Instant.parse("2026-11-02T10:00:00Z")
        );
    }
}