package mygov.task03;

import mygov.task03.domain.CompanySummary;
import mygov.task03.domain.Customer;
import mygov.task03.domain.CustomerWithCompanyResponse;
import mygov.task03.repository.JdbcCustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

    @Mock
    private JdbcClient.MappedQuerySpec<CustomerWithCompanyResponse> joinQuerySpec;

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

    @Test
    public void findByIdReturnsCustomerWhenPresent() {
        UUID testCustomerId = UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e");
        UUID testCompanyId = UUID.fromString("8be4df61-93ca-11d2-aa0d-00e098032b8c");

        Customer testCustomer = new Customer
                (testCustomerId, testCompanyId, "Rauf", "rauf@ada.az", "103898081", Instant.parse("2026-11-02T10:00:00Z"));

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("id", testCustomerId)).thenReturn(statementSpec);
        when(statementSpec.query(Customer.class)).thenReturn(customerQuerySpec);
        when(customerQuerySpec.optional()).thenReturn(Optional.of(testCustomer));

        Optional<Customer> resultCustomer = jdbcCustomerRepository.findById(testCustomerId);

        assertEquals(Optional.of(testCustomer), resultCustomer);
        verify(statementSpec).param("id", testCustomerId);
    }

    @Test
    public void findByIdReturnsEmptyWhenAbsent() {
        UUID testCustomerId = UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e");

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("id", testCustomerId)).thenReturn(statementSpec);
        when(statementSpec.query(Customer.class)).thenReturn(customerQuerySpec);
        when(customerQuerySpec.optional()).thenReturn(Optional.empty());

        Optional<Customer> resultCustomer = jdbcCustomerRepository.findById(testCustomerId);

        assertThat(resultCustomer.isEmpty());
        verify(statementSpec).param("id", testCustomerId);
    }

    @Test
    public void saveInsertsCustomerAndReturnsIt() {
        UUID testCustomerId = UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e");
        UUID testCompanyId = UUID.fromString("8be4df61-93ca-11d2-aa0d-00e098032b8c");

        Customer testCustomer = new Customer
                (testCustomerId, testCompanyId, "Rauf", "rauf@ada.az", "103898081", Instant.parse("2026-11-02T10:00:00Z"));

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("id", testCustomerId)).thenReturn(statementSpec);
        when(statementSpec.param("company_id", testCompanyId)).thenReturn(statementSpec);
        when(statementSpec.param("name", testCustomer.name())).thenReturn(statementSpec);
        when(statementSpec.param("email", testCustomer.email())).thenReturn(statementSpec);
        when(statementSpec.param("phone", testCustomer.phone())).thenReturn(statementSpec);
        when(statementSpec.param("created_at", Timestamp.from(testCustomer.createdAt()))).thenReturn(statementSpec);
        when(statementSpec.update()).thenReturn(1);

        Customer customer = jdbcCustomerRepository.save(testCustomer);

        assertSame(customer, testCustomer);
        verify(statementSpec).param("id", testCustomerId);
        verify(statementSpec).param("company_id", testCompanyId);
        verify(statementSpec).param("name", testCustomer.name());
        verify(statementSpec).param("email", testCustomer.email());
        verify(statementSpec).param("phone", testCustomer.phone());
        verify(statementSpec).param("created_at", Timestamp.from(testCustomer.createdAt()));
        verify(statementSpec).update();


    }

    @Test
    public void savePropagatesDatabaseException() {
        UUID testCustomerId = UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e");
        UUID testCompanyId = UUID.fromString("8be4df61-93ca-11d2-aa0d-00e098032b8c");

        Customer testCustomer = new Customer
                (testCustomerId, testCompanyId, "Rauf", "rauf@ada.az", "103898081", Instant.parse("2026-11-02T10:00:00Z"));

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("id", testCustomerId)).thenReturn(statementSpec);
        when(statementSpec.param("company_id", testCompanyId)).thenReturn(statementSpec);
        when(statementSpec.param("name", testCustomer.name())).thenReturn(statementSpec);
        when(statementSpec.param("email", testCustomer.email())).thenReturn(statementSpec);
        when(statementSpec.param("phone", testCustomer.phone())).thenReturn(statementSpec);
        when(statementSpec.param("created_at", Timestamp.from(testCustomer.createdAt()))).thenReturn(statementSpec);
        when(statementSpec.update()).thenThrow( new DataIntegrityViolationException(""));

        assertThrows(DataIntegrityViolationException.class, () -> jdbcCustomerRepository.save(testCustomer));
        verify(statementSpec).param("id", testCustomerId);
        verify(statementSpec).param("company_id", testCompanyId);
        verify(statementSpec).param("name", testCustomer.name());
        verify(statementSpec).param("email", testCustomer.email());
        verify(statementSpec).param("phone", testCustomer.phone());
        verify(statementSpec).param("created_at", Timestamp.from(testCustomer.createdAt()));
        verify(statementSpec).update();
    }

//    @Test
//    void findWithCompanyByIdMapsCustomerAndCompany() throws SQLException {
//        UUID customerId = UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e");
//        UUID companyId = UUID.fromString("8be4df61-93ca-11d2-aa0d-00e098032b8c");
//        Instant createdAt = Instant.parse("2026-11-02T10:00:00Z");
//
//        ResultSet row = mock(ResultSet.class);
//        when(row.getObject("customer_id", UUID.class)).thenReturn(customerId);
//        when(row.getString("customer_name")).thenReturn("Rauf");
//        when(row.getString("email")).thenReturn("rauf@ada.az");
//        when(row.getString("phone")).thenReturn("+994101234567");
//        when(row.getTimestamp("created_at")).thenReturn(Timestamp.from(createdAt));
//        when(row.getObject("company_id", UUID.class)).thenReturn(companyId);
//        when(row.getString("company_name")).thenReturn("Company_01");
//        when(row.getString("tax_number")).thenReturn("1701687402");
//
//        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
//        when(statementSpec.param("id", customerId)).thenReturn(statementSpec);
//        when(statementSpec.query(
//                ArgumentMatchers.<RowMapper<CustomerWithCompanyResponse>>any()
//        )).thenAnswer(invocation -> {
//            RowMapper<CustomerWithCompanyResponse> mapper = invocation.getArgument(0);
//            Runtime.Version joinQuerySpec;
//            when(joinQuerySpec.optional()).thenReturn(Optional.of(mapper.mapRow(row, 0)));
//            return joinQuerySpec;
//        });
//
//        var expected = new CustomerWithCompanyResponse(
//                customerId, "Rauf", "rauf@ada.az", "+994101234567",
//                new CompanySummary(companyId, "Company_01", "1701687402"),
//                createdAt
//        );
//
//        assertEquals(Optional.of(expected),
//                jdbcCustomerRepository.findWithCompanyById(customerId));
//        verify(statementSpec).param("id", customerId);
//    }



}
