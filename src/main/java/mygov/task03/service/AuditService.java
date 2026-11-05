package mygov.task03.service;

import mygov.task03.domain.AuditLog;
import mygov.task03.repository.AuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditRepository auditRepository;
    private final TransactionTemplate transactionTemplate;

    public AuditService(AuditRepository auditRepository, TransactionTemplate transactionTemplate) {
        this.auditRepository = auditRepository;
        this.transactionTemplate = transactionTemplate;
    }

    public void logAudit(UUID entityId, String action, String details) {
        String cleanedDetails = details == null ? null : details.strip();

        AuditLog auditLog = new AuditLog(
                UUID.randomUUID(),
                entityId,
                action,
                cleanedDetails,
                Instant.now()
        );


        transactionTemplate.execute(status -> {
            auditRepository.insert(auditLog);
            return null;
        });

    }

    @Transactional(readOnly = true)
    public List<AuditLog> findByEntityId(UUID entityId) {
        return auditRepository.findByEntityId(entityId);
    }


}
