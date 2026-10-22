package mygov.task03.repository;

import mygov.task03.model.Company;

import java.util.UUID;

public interface CompanyRepository {

    void save(Company company);

    Company findById(UUID id);

    Company findByName(String name);

    boolean existsByTaxNumber(String taxNumber);
}
