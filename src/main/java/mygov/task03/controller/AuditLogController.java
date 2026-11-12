package mygov.task03.controller;

import mygov.task03.domain.AuditLog;
import mygov.task03.service.AuditService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {
    private final AuditService auditService;

    public AuditLogController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/{entityId}")
    public List<AuditLog> getAuditLogList(@PathVariable UUID entityId) {
        return auditService.findByEntityId(entityId);
    }
}
