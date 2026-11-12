package mygov.task03;

import mygov.task03.repository.CompanyRepository;
import mygov.task03.service.CorporateRegistrationService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

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


}
