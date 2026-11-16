package mygov.task03.service;

import mygov.task03.domain.AuditLog;
import mygov.task03.domain.Company;
import mygov.task03.domain.CompanySummary;
import mygov.task03.domain.Customer;
import mygov.task03.domain.CustomerWithCompanyResponse;
import mygov.task03.dto.RegisterCorporateClientRequest;
import mygov.task03.exceptions.DuplicateResourceException;
import mygov.task03.repository.CompanyRepository;
import mygov.task03.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CorporateRegistrationService {

    private static final String COMPANY_REGISTERED = "COMPANY_REGISTERED";

    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final AuditService auditService;

    public CorporateRegistrationService(
            CompanyRepository companyRepository,
            CustomerRepository customerRepository,
            AuditService auditService) {
        this.companyRepository = companyRepository;
        this.customerRepository = customerRepository;
        this.auditService = auditService;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public CustomerWithCompanyResponse registerCorporateClient(
            RegisterCorporateClientRequest request) {
        if (companyRepository.findByName(request.companyName()).isPresent()) {
            throw new DuplicateResourceException("This company name already exists");
        }

        if (companyRepository.existsByTaxNumber(request.taxNumber())) {
            throw new DuplicateResourceException("Tax number is already registered");
        }

        if (customerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        Instant now = Instant.now();

        Company company = companyRepository.save(new Company(
                UUID.randomUUID(),
                request.companyName(),
                request.taxNumber(),
                now
        ));

        Customer customer = customerRepository.save(new Customer(
                UUID.randomUUID(),
                company.id(),
                request.customerName(),
                request.email(),
                request.phone(),
                now
        ));

        auditService.writeAudit(new AuditLog(
                UUID.randomUUID(),
                company.id(),
                COMPANY_REGISTERED,
                "Company registered",
                now
        ));

        CompanySummary companySummary = new CompanySummary(
                company.id(),
                company.name(),
                company.taxNumber()
        );

        return new CustomerWithCompanyResponse(
                customer.id(),
                customer.name(),
                customer.email(),
                customer.phone(),
                companySummary,
                customer.createdAt()
        );
    }
}