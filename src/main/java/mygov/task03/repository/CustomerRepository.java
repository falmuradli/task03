package mygov.task03.repository;

import mygov.task03.domain.Customer;
import mygov.task03.domain.CustomerWithCompanyResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID id);

    List<Customer> findByCompanyId(UUID companyId, int limit, int offset);

    List<Customer> findAllPaged(int limit, int offset);

    long countAll();

    long countByCompanyId(UUID companyId);

    boolean existsByEmail(String email);

    Optional<CustomerWithCompanyResponse> findWithCompanyById(UUID id);


}
