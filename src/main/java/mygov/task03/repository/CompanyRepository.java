package mygov.task03.repository;

import mygov.task03.domain.Company;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {

    Company save(Company company);

    Optional<Company> findById(UUID id);

    Optional<Company> findByName(String name);

    boolean existsByTaxNumber(String taxNumber);
}
