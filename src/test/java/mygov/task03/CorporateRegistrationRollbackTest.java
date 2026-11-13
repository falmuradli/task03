package mygov.task03;

import mygov.task03.dto.RegisterCorporateClientRequest;
import mygov.task03.repository.CompanyRepository;
import mygov.task03.service.CorporateRegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class CorporateRegistrationRollbackTest {

    private final CorporateRegistrationService corporateRegistrationService;

    private final CompanyRepository companyRepository;

    public CorporateRegistrationRollbackTest(CorporateRegistrationService corporateRegistrationService, CompanyRepository companyRepository) {
        this.corporateRegistrationService = corporateRegistrationService;
        this.companyRepository = companyRepository;
    }


    @Test
    public void rollsBackCompanyWhenCustomerInsertFails() {
        RegisterCorporateClientRequest registerCorporateClientRequest = new RegisterCorporateClientRequest(
                "Western Tech Solutions",
                "1098738561",
                "Roll Back",
                "rollback-test@example.com",
                "+994144469780435123658568874462212355768568945211243552"
        );

        assertThrows(DataIntegrityViolationException.class, () -> corporateRegistrationService.registerCorporateClient(registerCorporateClientRequest));
        assertTrue(companyRepository.findByName(registerCorporateClientRequest.companyName()).isEmpty(),
                "The company should not remain after the customer insert fails");

    }
}
