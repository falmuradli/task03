package mygov.task03.repository;

import mygov.task03.domain.Company;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public class JdbcCompanyRepository implements CompanyRepository {

    private final JdbcClient jdbcClient;

    public JdbcCompanyRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Company save(Company company) {
         jdbcClient
                .sql("""
                        INSERT INTO companies (id, name, tax_number, created_at) VALUES (:id, :name, :tax_number, :created_at)""")
                .param("id", company.id())
                .param("name", company.name())
                .param("tax_number", company.taxNumber())
                .param("created_at", Timestamp.from(company.createdAt()))
                .update();
         return company;
    }


    @Override
    public Optional<Company> findById(UUID id) {
        return jdbcClient
                .sql(""" 
                        SELECT id, name, tax_number, created_at FROM companies WHERE id = : id""")
                .param("id", id)
                .query(Company.class)
                .optional();
    }

    @Override
    public Optional<Company> findByName(String name) {
        return jdbcClient
                .sql("""
                       SELECT id, name, tax_number, created_at FROM companies WHERE name : name""")
                .param("name", name)
                .query(Company.class)
                .optional();
    }

    @Override
    public boolean existsByTaxNumber(String taxNumber) {
        return jdbcClient
                .sql("""
                        SELECT id, name, tax_number, created_at FROM companies WHERE taxNumber = :taxNumber""")
                .param("taxNumber", taxNumber)
                .query(Boolean.class)
                .single();
    }
}
