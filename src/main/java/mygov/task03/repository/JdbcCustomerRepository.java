package mygov.task03.repository;

import mygov.task03.domain.CompanySummary;
import mygov.task03.domain.Customer;
import mygov.task03.domain.CustomerWithCompanyResponse;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcCustomerRepository implements CustomerRepository {

    private final JdbcClient jdbcClient;

    public JdbcCustomerRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }


    @Override
    public Customer save(Customer customer) {
        jdbcClient
                .sql("""
                        INSERT INTO customers(id, companyId, name, email, phone) 
                        VALUES (:id, :companyId, :email, :phone, :createdAt)""")
                .param("id", customer.id())
                .param("company_id", customer.companyId())
                .param("name", customer.name())
                .param("email", customer.email())
                .param("phone", customer.phone())
                .param("created_at", Timestamp.from(customer.createdAt()))
                .update();
        return customer;
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jdbcClient
                .sql("""
                        SELECT id, company_id, name, email, phone, created_at FROM customers WHERE id = :id""")
                .param("id", id)
                .query(Customer.class)
                .optional();
    }

    @Override
    public List<Customer> findByCompanyId(UUID companyId, int limit, int offset) {
        return jdbcClient
                .sql("""
                        SELECT id, company_id, name, email, phone, created_at FROM customers WHERE companyId = :companyId
                            ORDER BY created_at, id 
                            LIMIT :limit OFFSET :offset""" )
                .param("companyId", companyId)
                .param("limit", limit)
                .param("offset", offset)
                .query(Customer.class)
                .list();
    }

    @Override
    public List<Customer> findAllPaged(int limit, int offset) {
        return jdbcClient
                .sql("""
                        SELECT id, company_id, name, email, phone, created_at FROM customers 
                        ORDER BY created_at, id 
                        LIMIT :limit OFFSET :offset""")
                .param("limit", limit)
                .param("offset", offset)
                .query(Customer.class)
                .list();
    }

    @Override
    public long countAll() {
        return jdbcClient
                .sql("""
                        SELECT COUNT(*) FROM customers""")
                .query(Long.class)
                .single();
    }

    @Override
    public long countByCompanyId(UUID companyId) {
        return jdbcClient
                .sql("""
                        SELECT COUNT(*) FROM customers WHERE companyId = :companyId""")
                .param("company_id", companyId)
                .query(Long.class)
                .single();
    }

    @Override
    public boolean existsByEmail(String email) {
        return jdbcClient
                .sql("""
                        SELECT EXISTS(SELECT 1 FROM customers WHERE email = :email)""")
                .param("email", email)
                .query(Boolean.class)
                .single();
    }

    @Override
    public Optional<CustomerWithCompanyResponse> findWithCompanyById(UUID id) {
        return jdbcClient
                .sql("""
                        SELECT 
                            c.id AS customer_id,
                            c.name AS customer_name,
                            c.email,
                            c.phone,
                            c.created_at,
                            co.id AS company_id,
                            co.name AS company_name,
                            co.tax_number
                        FROM customers c
                        JOIN companies co ON c.company_id = co.id
                        WHERE c.id = :id""")
                .param("id", id)
                .query((rs, rowNum) -> new CustomerWithCompanyResponse(
                    rs.getObject("customer_id", UUID.class),
                    rs.getString("customer_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    new CompanySummary(
                        rs.getObject("company_id", UUID.class),
                        rs.getString("company_name"),
                        rs.getString("tax_number")
                    ),
                    rs.getTimestamp("created_at").toInstant()
        ))
                .optional();
    }
}
