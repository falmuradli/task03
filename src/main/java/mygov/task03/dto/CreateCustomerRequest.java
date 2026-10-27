package mygov.task03.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreateCustomerRequest(

        @NotNull
        UUID companyId,

        @NotBlank
        @Size(min = 2, max = 100)
        String name,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Pattern(regexp = "^\\+994\\d{9}$")
        String phone
) {}
