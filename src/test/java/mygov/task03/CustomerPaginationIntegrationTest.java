package mygov.task03;

import mygov.task03.domain.Company;
import mygov.task03.domain.Customer;
import mygov.task03.repository.CompanyRepository;
import mygov.task03.repository.CustomerRepository;
import mygov.task03.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class CustomerPaginationIntegrationTest {

    private final CustomerService customerService;

    private final CompanyRepository companyRepository;

    private final CustomerRepository customerRepository;

    public CustomerPaginationIntegrationTest(CustomerService customerService, CompanyRepository companyRepository, CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.companyRepository = companyRepository;
        this.customerRepository = customerRepository;
    }

    @Test
    public void findCustomersReturnsTwoPagesForRequestedCompany() {
        Instant createdAt = Instant.parse("2026-11-02T10:00:00Z");

        UUID targetCompanyId = UUID.randomUUID();
        UUID otherCompanyId = UUID.randomUUID();

        companyRepository.save(new Company(
                targetCompanyId, "Pagination Test Company", "9000000001", createdAt));

        companyRepository.save(new Company(
                otherCompanyId, "Pagination Control Company", "9000000002", createdAt));

        for (int i = 1; i <= 12; i++) {

            customerRepository.save(new Customer(
                    UUID.randomUUID(),
                    targetCompanyId,
                    "Pagination Customer " + i,
                    "pagination-" + i + "@example.com",
                    "+994501234567",
                    createdAt.plusSeconds(i)));
        }

        customerRepository.save(new Customer(
                UUID.randomUUID(),
                otherCompanyId,
                "Other Company Customer",
                "pagination-other@example.com",
                "+994501234567",
                createdAt
        ));

        var firstPage = customerService.findCustomers(0, 10, targetCompanyId);
        var secondPage = customerService.findCustomers(1, 10, targetCompanyId);

        assertEquals(10, firstPage.content().size());
        assertEquals(2, secondPage.content().size());
        assertEquals(12L, firstPage.totalElements());
        assertEquals(12L, secondPage.totalElements());
        assertEquals(2, firstPage.totalPages());
        assertEquals(2, secondPage.totalPages());

        var firstPageIds = firstPage.content().stream()
                .map(Customer::id)
                .collect(Collectors.toSet());
        assertTrue(
                secondPage.content().stream()
                        .noneMatch(customer -> firstPageIds.contains(customer.id())),
                "The two pages should not share any customer IDs");
        assertTrue(
                Stream.concat(firstPage.content().stream(), secondPage.content().stream())
                        .allMatch(customer -> targetCompanyId.equals(customer.companyId())),
                "Every returned customer should belong to the requested company");
    }

    @Test
    @Transactional
    void findCustomersReturnsPagesAcrossAllCompanies() {
        long startingCount = customerRepository.countAll();

        Instant createdAt = Instant.parse("2026-11-02T10:00:00Z");
        UUID firstCompanyId = UUID.randomUUID();
        UUID secondCompanyId = UUID.randomUUID();

        companyRepository.save(new Company(
                firstCompanyId, "All Pages Company A", "9000000003", createdAt
        ));
        companyRepository.save(new Company(
                secondCompanyId, "All Pages Company B", "9000000004", createdAt
        ));

        for (int i = 1; i <= 13; i++) {
            UUID companyId = i <= 12 ? firstCompanyId : secondCompanyId;

            customerRepository.save(new Customer(
                    UUID.randomUUID(),
                    companyId,
                    "All Pages Customer " + i,
                    "all-pages-" + i + "@example.com",
                    "+994501234567",
                    createdAt.plusSeconds(i)
            ));
        }

        int pageSize = 10;
        long expectedTotal = startingCount + 13;
        int expectedTotalPages = Math.toIntExact(Math.ceilDiv(expectedTotal, pageSize));

        var firstPage = customerService.findCustomers(0, pageSize, null);
        var secondPage = customerService.findCustomers(1, pageSize, null);

        assertEquals(10, firstPage.content().size());
        assertEquals((int) Math.min(pageSize, expectedTotal - pageSize),
                secondPage.content().size());

        assertEquals(expectedTotal, firstPage.totalElements());
        assertEquals(expectedTotal, secondPage.totalElements());
        assertEquals(expectedTotalPages, firstPage.totalPages());
        assertEquals(expectedTotalPages, secondPage.totalPages());

        var firstPageIds = firstPage.content().stream()
                .map(Customer::id)
                .collect(Collectors.toSet());

        assertTrue(
                secondPage.content().stream()
                        .noneMatch(customer -> firstPageIds.contains(customer.id())),
                "The two pages should not share any customer IDs"
        );
    }

}
