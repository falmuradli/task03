package mygov.task03;

import mygov.task03.domain.Customer;
import mygov.task03.repository.JdbcCustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.simple.JdbcClient;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JdbcCustomerRepositoryTest {

    @Mock
    private JdbcClient jdbcClient;

    @Mock
    private JdbcClient.StatementSpec statementSpec;

    @Mock
    private JdbcClient.MappedQuerySpec<Long> longQuerySpec;

    @Mock
    private JdbcClient.MappedQuerySpec<Boolean> booleanQuerySpec;

    @Mock
    private JdbcClient.MappedQuerySpec<Customer> customerQuerySpec;

    @InjectMocks
    private JdbcCustomerRepository jdbcCustomerRepository;

    @Test
    public void findAllPagedReturnsCustomers() {
        UUID testCustomerId = UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e");
        UUID testCompanyId = UUID.fromString("8be4df61-93ca-11d2-aa0d-00e098032b8c");

        Customer testCustomer = new Customer
                (testCustomerId, testCompanyId, "Rauf", "rauf@ada.az", "103898081", Instant.parse("2026-11-02T10:00:00Z"));

        List<Customer> customerList = new ArrayList<>();
        customerList.add(testCustomer);

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("limit", 10)).thenReturn(statementSpec);
        when(statementSpec.param("offset", 20)).thenReturn(statementSpec);
        when(statementSpec.query(Customer.class)).thenReturn(customerQuerySpec);
        when(customerQuerySpec.list()).thenReturn(customerList);

        List<Customer> result = jdbcCustomerRepository.findAllPaged(10,20);

        assertEquals(customerList, result);
        verify(statementSpec).param("limit", 10);
        verify(statementSpec).param("offset", 20);
    }

    @Test
    public void findByCompanyIdReturnsCustomers() {
        UUID testCustomerId = UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e");
        UUID testCompanyId = UUID.fromString("8be4df61-93ca-11d2-aa0d-00e098032b8c");

        Customer testCustomer = new Customer
                (testCustomerId, testCompanyId, "Rauf", "rauf@ada.az", "103898081", Instant.parse("2026-11-02T10:00:00Z"));

        List<Customer> customerList = new ArrayList<>();
        customerList.add(testCustomer);

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("companyId", testCompanyId)).thenReturn(statementSpec);
        when(statementSpec.param("limit", 10)).thenReturn(statementSpec);
        when(statementSpec.param("offset", 20)).thenReturn(statementSpec);
        when(statementSpec.query(Customer.class)).thenReturn(customerQuerySpec);
        when(customerQuerySpec.list()).thenReturn(customerList);

        List<Customer> result = jdbcCustomerRepository.findByCompanyId(testCompanyId, 10, 20);
        assertEquals(customerList, result);
        verify(statementSpec).param("companyId", testCompanyId);
        verify(statementSpec).param("limit", 10);
        verify(statementSpec).param("offset", 20);



    }

    @Test
    public void countByCompanyIdReturnCount() {
        UUID testCustomerCompanyId = UUID.fromString("8be4df61-93ca-11d2-aa0d-00e098032b8c");


        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("companyId", testCustomerCompanyId)).thenReturn(statementSpec);
        when(statementSpec.query(Long.class)).thenReturn(longQuerySpec);
        when(longQuerySpec.single()).thenReturn(3L);

        Long count = jdbcCustomerRepository.countByCompanyId(testCustomerCompanyId);
        assertEquals(3L, count);
        verify(statementSpec).param("companyId", testCustomerCompanyId);
    }

    @Test
    public void countAllReturnsCount() {

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.query(Long.class)).thenReturn(longQuerySpec);
        when(longQuerySpec.single()).thenReturn(5L);

        Long count = jdbcCustomerRepository.countAll();
        assertEquals(5L, count);
    }

    @Test
    public void existsByEmailReturnsTrueWhenPresent() {

        String testEmail = "rauf@ada.edu.az";

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("email", testEmail)).thenReturn(statementSpec);
        when(statementSpec.query(Boolean.class)).thenReturn(booleanQuerySpec);
        when(booleanQuerySpec.single()).thenReturn(true);

        boolean result = jdbcCustomerRepository.existsByEmail(testEmail);

        assertTrue(result);
        verify(statementSpec).param("email", testEmail);
    }

    @Test
    public void existsByEmailReturnsFalseWhenAbsent() {
        String testEmail = "rauf@ada.edu.az";

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("email", testEmail)).thenReturn(statementSpec);
        when(statementSpec.query(Boolean.class)).thenReturn(booleanQuerySpec);
        when(booleanQuerySpec.single()).thenReturn(false);

        boolean result = jdbcCustomerRepository.existsByEmail(testEmail);

        assertFalse(result);
        verify(statementSpec).param("email", testEmail);
    }

}
