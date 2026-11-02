package mygov.task03;

import mygov.task03.domain.Company;
import mygov.task03.repository.JdbcCompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.TestConstructor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ExtendWith(MockitoExtension.class)
public class JdbcCompanyRepositoryTest {

    @Mock
    private JdbcClient jdbcClient;

    @Mock
    private JdbcClient.StatementSpec statementSpec;

    @Mock
    private JdbcClient.MappedQuerySpec<Boolean> booleanQuerySpec;

    @Mock
    private JdbcClient.MappedQuerySpec<Company> companyQuerySpec;

    @InjectMocks
    private JdbcCompanyRepository jdbcCompanyRepository;


    @Test
    public void existsByTaxNumberReturnsTrueWhenPresent() {

        String taxNumber = "1701687402";
        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("taxNumber", taxNumber))
                .thenReturn(statementSpec);
        when(statementSpec.query(Boolean.class)).thenReturn(booleanQuerySpec);
        when(booleanQuerySpec.single()).thenReturn(true);

        boolean result = jdbcCompanyRepository.existsByTaxNumber(taxNumber);
        assertTrue(result);
        verify(statementSpec).param("taxNumber", taxNumber);
    }

    @Test
    public void existsByTaxNumberReturnsFalseWhenAbsent() {
        String taxNumber = "1234569082";

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("taxNumber", taxNumber))
                .thenReturn(statementSpec);
        when(statementSpec.query(Boolean.class)).thenReturn(booleanQuerySpec);
        when(booleanQuerySpec.single()).thenReturn(false);

        boolean result = jdbcCompanyRepository.existsByTaxNumber(taxNumber);
        assertFalse(result);
        verify(statementSpec).param("taxNumber", taxNumber);
    }

    @Test
    public void findByIdReturnsCompanyWhenPresent() {

        UUID randomUUId = UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e");
        Company testCompany = new Company
                (randomUUId, "faigCompany", "1234569082", Instant.parse("2026-11-02T10:00:00Z"));

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("id", randomUUId)).thenReturn(statementSpec);
        when(statementSpec.query(Company.class)).thenReturn(companyQuerySpec);
        when(companyQuerySpec.optional()).thenReturn(Optional.of(testCompany));

        Optional<Company> result = jdbcCompanyRepository.findById(randomUUId);

        assertEquals(Optional.of(testCompany), result);
        verify(statementSpec).param("id", randomUUId);
    }
}
