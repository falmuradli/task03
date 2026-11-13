package mygov.task03;

import mygov.task03.controller.CorporateRegistrationController;
import mygov.task03.domain.CompanySummary;
import mygov.task03.domain.CustomerWithCompanyResponse;
import mygov.task03.dto.RegisterCorporateClientRequest;
import mygov.task03.exceptions.DuplicateResourceException;
import mygov.task03.service.AuditService;
import mygov.task03.service.CorporateRegistrationService;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CorporateRegistrationController.class)
public class CorporateRegistrationControllerTest {

    @MockitoBean
    private CorporateRegistrationService corporateRegistrationService;

    @MockitoBean
    private AuditService auditService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void registerReturns400WhenCustomerEmailIsBlank() throws Exception {

        String requestBody = """
                {
                    "companyName": "Caspian Tech Solutions",
                    "taxNumber": "5937462910",
                    "customerName": "Leyla Aliyeva",
                    "customerEmail": "",
                    "customerPhone": "+9944501234567"
                }""";

        this.mockMvc.perform(post("/api/v1/corporate/register")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].field").value(hasItem("customerEmail")));
        verifyNoInteractions(corporateRegistrationService, auditService);

    }

    @Test
    public void registerReturns201WhenRequestIsValid() throws Exception {
        UUID testCustomerId = UUID.fromString("9ca1c1bd-6b78-438a-ae81-bf5ecfdfdb9e");
        UUID testCompanyId = UUID.fromString("8be4df61-93ca-11d2-aa0d-00e098032b8c");

        CompanySummary companySummary = new CompanySummary(testCompanyId, "Caspian Tech Solutions", "123567891");
        CustomerWithCompanyResponse customerWithCompanyResponse = new CustomerWithCompanyResponse(
                testCustomerId,
                "Leyla",
                "leyla@example.com",
                "+994501234567",
                companySummary,
                Instant.parse("2026-11-02T10:00:00Z"));

        String requestBody = """
                {
                    "companyName": "Caspian Tech Solutions",
                    "taxNumber": "5937462970",
                    "customerName": "Leyla Aliyeva",
                    "customerEmail": "leyla@example.com",
                    "customerPhone": "+994501234567"
                }""";

        when(corporateRegistrationService.registerCorporateClient(
                any(RegisterCorporateClientRequest.class)))
                .thenReturn(customerWithCompanyResponse);

        this.mockMvc.perform(post("/api/v1/corporate/register")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/customers/" + testCustomerId))
                .andExpect(jsonPath("$.id").value(testCustomerId.toString()))
                .andExpect(jsonPath("$.company.id").value(testCompanyId.toString()));
    }

    @Test
    public void registerReturns409WhenEmailAlreadyExists() throws Exception {
        String requestBody = """
                {
                    "companyName": "Caspian Tech Solutions",
                    "taxNumber": "5937462910",
                    "customerName": "Leyla Aliyeva",
                    "customerEmail": "leyla@example.com",
                    "customerPhone": "+994501234567"
                }""";

        when(corporateRegistrationService.registerCorporateClient(
                any(RegisterCorporateClientRequest.class)))
                .thenThrow(new DuplicateResourceException("Email is already registered"));

        this.mockMvc.perform(post("/api/v1/corporate/register")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Email is already registered"));


    }
}
