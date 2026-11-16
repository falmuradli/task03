package mygov.task03;

import mygov.task03.domain.AuditLog;
import mygov.task03.dto.RegisterCorporateClientRequest;
import mygov.task03.repository.CompanyRepository;
import mygov.task03.repository.CustomerRepository;
import mygov.task03.repository.JdbcAuditRepository;
import mygov.task03.service.CorporateRegistrationService;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class CorporateRegistrationAuditRollbackTest {

    private final CorporateRegistrationService corporateRegistrationService;

    private final CompanyRepository companyRepository;

    private final CustomerRepository customerRepository;

    @MockitoBean
    private final JdbcAuditRepository auditRepository;

    public CorporateRegistrationAuditRollbackTest(CorporateRegistrationService corporateRegistrationService, CompanyRepository companyRepository,
                                             CustomerRepository customerRepository, JdbcAuditRepository auditRepository) {
        this.corporateRegistrationService = corporateRegistrationService;
        this.companyRepository = companyRepository;
        this.customerRepository = customerRepository;
        this.auditRepository = auditRepository;
    }

    @Test
    void rollsBackCompanyAndCustomerWhenAuditInsertFails() {
        String taxNumber = "7816452309";
        String email = "audit-rollback@example.com";

        RegisterCorporateClientRequest request = new RegisterCorporateClientRequest(
                        "Audit Rollback Company",
                        taxNumber,
                        "Leyla Aliyeva",
                        email,
                        "+994501234567");

        doThrow(new DataIntegrityViolationException("Simulated audit insert failure"))
                .when(auditRepository).insert(any(AuditLog.class));

        assertThrows(DataIntegrityViolationException.class,
                () -> corporateRegistrationService.registerCorporateClient(request));

        verify(auditRepository).insert(any(AuditLog.class));

        assertFalse(companyRepository.existsByTaxNumber(taxNumber),
                "The company should have been rolled back");

        assertFalse(customerRepository.existsByEmail(email),
                "The customer should have been rolled back");

    }

}
