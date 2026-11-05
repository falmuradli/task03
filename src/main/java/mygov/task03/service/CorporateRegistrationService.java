package mygov.task03.service;

import com.sun.jdi.request.DuplicateRequestException;
import mygov.task03.domain.Company;
import mygov.task03.domain.CompanySummary;
import mygov.task03.domain.Customer;
import mygov.task03.domain.CustomerWithCompanyResponse;
import mygov.task03.dto.RegisterCorporateClientRequest;
import mygov.task03.exceptions.DuplicateResourceException;
import mygov.task03.repository.CompanyRepository;
import mygov.task03.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CorporateRegistrationService {

    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;

    public CorporateRegistrationService(CompanyRepository companyRepository, CustomerRepository customerRepository) {
        this.companyRepository = companyRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerWithCompanyResponse registerCorporateClient(RegisterCorporateClientRequest registerCorporateClientRequest) {
        if(companyRepository.findByName(registerCorporateClientRequest.companyName()).isPresent()) {
            throw new DuplicateResourceException("This company name already exists");
        }

        if(companyRepository.existsByTaxNumber(registerCorporateClientRequest.taxNumber())) {
            throw new DuplicateResourceException("Tax number is already registered");
        }

        if(customerRepository.existsByEmail(registerCorporateClientRequest.customerEmail())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        Instant now = Instant.now();

        Company company = companyRepository.save
                (new Company(UUID.randomUUID(),
                        registerCorporateClientRequest.companyName(),
                        registerCorporateClientRequest.taxNumber(),
                        now
                ));

        Customer customer = customerRepository.save
                (new Customer(UUID.randomUUID(),
                        company.id(),
                        registerCorporateClientRequest.customerName(),
                        registerCorporateClientRequest.customerEmail(),
                        registerCorporateClientRequest.customerPhone(),
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
