package mygov.task03.controller;


import jakarta.validation.Valid;
import mygov.task03.domain.CustomerWithCompanyResponse;
import mygov.task03.dto.RegisterCorporateClientRequest;
import mygov.task03.service.AuditService;
import mygov.task03.service.CorporateRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/corporate")
public class CorporateRegistrationController {

    private final CorporateRegistrationService corporateRegistrationService;

    public CorporateRegistrationController(CorporateRegistrationService corporateRegistrationService,
                                           AuditService auditService) {
        this.corporateRegistrationService = corporateRegistrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerWithCompanyResponse> setCompany(@Valid @RequestBody
                                                                      RegisterCorporateClientRequest
                                                                              registerCorporateClientRequest) {

        CustomerWithCompanyResponse response =  corporateRegistrationService
                .registerCorporateClient(registerCorporateClientRequest);


        URI location = URI.create("/api/v1/customers/" + response.id());

        return ResponseEntity.created(location).body(response);

    }


}
