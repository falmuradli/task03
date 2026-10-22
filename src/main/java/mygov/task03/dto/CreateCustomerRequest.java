package mygov.task03.dto;

import java.util.UUID;

public record CreateCustomerRequest(

        UUID companyId,

        String name,

        String email,

        String phone
) {}
