package mygov.task03.service;

import mygov.task03.domain.Customer;
import mygov.task03.domain.CustomerWithCompanyResponse;
import mygov.task03.dto.PageResponse;
import mygov.task03.exceptions.ResourceNotFoundException;
import mygov.task03.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public CustomerWithCompanyResponse findById(UUID id) {

        return customerRepository.findWithCompanyById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    @Transactional(readOnly = true)
    public PageResponse<Customer> findCustomers(int page, int size, UUID companyId) {
        long totalCount;
        List<Customer> content;

        if((page < 0) || (1 > size  || size > 100)) throw new IllegalArgumentException("Such inputs are not possible");

        long offset = (long) page *size;
        if(companyId == null) {
            content = customerRepository.findAllPaged(size, (int) offset);
            totalCount = customerRepository.countAll();
        } else {
            content = customerRepository.findByCompanyId(companyId, size, (int) offset);
            totalCount = customerRepository.countByCompanyId(companyId);
        }

        int totalPages = (int) Math.ceil((double) totalCount / size);

        return new PageResponse<>(content, page, size, totalCount, totalPages);
    }


}
