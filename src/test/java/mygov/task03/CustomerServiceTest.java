package mygov.task03;

import mygov.task03.domain.CompanySummary;
import mygov.task03.domain.Customer;
import mygov.task03.domain.CustomerWithCompanyResponse;
import mygov.task03.dto.PageResponse;
import mygov.task03.exceptions.ResourceNotFoundException;
import mygov.task03.repository.CustomerRepository;
import mygov.task03.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    public void findCustomerReturnsPageWithoutCompanyFilter() {
        UUID testCustomerId = UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e");
        UUID testCompanyId = UUID.fromString("8be4df61-93ca-11d2-aa0d-00e098032b8c");
        Customer testCustomer = new Customer(
                testCustomerId,
                testCompanyId,
                "Rauf",
                "rafu@ada.edu.az",
                "0103898081",
                Instant.parse("2026-11-02T10:00:00Z"));

        List<Customer> expectedContent = List.of(testCustomer);

        when(customerRepository.findAllPaged(10, 0))
                .thenReturn(expectedContent);
        when(customerRepository.countAll()).thenReturn(23L);

        PageResponse<Customer> result = customerService.findCustomers(0, 10, null);

        assertEquals(expectedContent, result.content());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(23L, result.totalElements());
        assertEquals(3, result.totalPages());


        verify(customerRepository).findAllPaged(10, 0);
        verify(customerRepository).countAll();
    }

    @Test
    public void findCustomersReturnsPageForCompany() {
        UUID testCustomerId = UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e");
        UUID testCompanyId = UUID.fromString("8be4df61-93ca-11d2-aa0d-00e098032b8c");
        Customer testCustomer = new Customer(
                testCustomerId,
                testCompanyId,
                "Rauf",
                "rafu@ada.edu.az",
                "0103898081",
                Instant.parse("2026-11-02T10:00:00Z"));

        List<Customer> expectedContent = List.of(testCustomer);

        when(customerRepository.findByCompanyId(testCompanyId, 10, 10))
                .thenReturn(expectedContent);
        when(customerRepository.countByCompanyId(testCompanyId))
                .thenReturn(12L);

        PageResponse<Customer> result = customerService.findCustomers(1, 10, testCompanyId);

        assertEquals(expectedContent, result.content());
        assertEquals(1, result.page());
        assertEquals(10, result.size());
        assertEquals(12, result.totalElements());
        assertEquals(2, result.totalPages());

        verify(customerRepository).findByCompanyId(testCompanyId, 10, 10);
        verify(customerRepository).countByCompanyId(testCompanyId);
    }

    @Test
    public void findCustomerRejectsNegativePage() {

        assertThrows(IllegalArgumentException.class, () ->
                customerService.findCustomers(-1, 10, null));

        verifyNoInteractions(customerRepository);
    }

    @Test
    public void findCustomerRejectsZeroSize() {
        assertThrows(IllegalArgumentException.class, () ->
                customerService.findCustomers(0, 0, null));
        verifyNoInteractions(customerRepository);
    }

    @Test
    public void findCustomersRejectsSizeAboveMaximum() {
        assertThrows(IllegalArgumentException.class, () ->
                customerService.findCustomers(0, 101, null));
        verifyNoInteractions(customerRepository);
    }

    @Test
    public void findCustomersReturnsEmptyPageWhenNoCustomersMatch() {
        when(customerRepository.findAllPaged(10, 0))
                .thenReturn(List.of());
        when(customerRepository.countAll()).thenReturn(0L);

        PageResponse<Customer> result = customerService.findCustomers(0, 10, null);

        assertTrue(result.content().isEmpty());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(0L, result.totalElements());
        assertEquals(0, result.totalPages());
    }

    @Test
    public void findByIdThrowsWhenCustomerNotFound() {
        UUID testCustomerId = UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e");

        when(customerRepository.findWithCompanyById(testCustomerId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.findById(testCustomerId));
        verify(customerRepository).findWithCompanyById(testCustomerId);
    }

    @Test
    public void findByIdReturnsCustomerWhenFound() {
        CompanySummary companySummary = new CompanySummary(
                UUID.fromString("8be4df61-93ca-11d2-aa0d-00e098032b8c"),
                "JavaComp",
                "1234569082");

        CustomerWithCompanyResponse response = new CustomerWithCompanyResponse(
                UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e"),
                "Rauf",
                "rauf@ada.az",
                "103898081",
                companySummary,
                Instant.parse("2026-11-02T10:00:00Z"));

        when(customerRepository.findWithCompanyById(UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e")))
                .thenReturn(Optional.of(response));

        CustomerWithCompanyResponse result = customerService.findById(UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e"));

        assertSame(response, result);
        verify(customerRepository).findWithCompanyById(UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e"));
    }
}
