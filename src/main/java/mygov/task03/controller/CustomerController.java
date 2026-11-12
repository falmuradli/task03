package mygov.task03.controller;

import mygov.task03.domain.Customer;
import mygov.task03.domain.CustomerWithCompanyResponse;
import mygov.task03.dto.PageResponse;
import mygov.task03.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public PageResponse<Customer> getCustomer(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size,
            @RequestParam(required = false) UUID companyId) {
        return customerService.findCustomers(page, size, companyId);
    }

    @GetMapping("/{id}")
    public CustomerWithCompanyResponse getCustomer(@PathVariable UUID id) {
        return customerService.findById(id);
    }

}
