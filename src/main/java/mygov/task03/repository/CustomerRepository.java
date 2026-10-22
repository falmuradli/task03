package mygov.task03.repository;

import mygov.task03.model.Customer;

import java.util.UUID;

public interface CustomerRepository {

    void save(Customer customer);

    Customer findById(UUID id);

    Customer findByCompanyId(UUID companyId, int limit, int offset);

    int countAll();

    int countByCompanyId(UUID companyId);

    boolean existsByEmail(String email);


}
