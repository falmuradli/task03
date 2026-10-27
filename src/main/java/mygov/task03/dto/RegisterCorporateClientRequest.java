package mygov.task03.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterCorporateClientRequest(
        @NotBlank
        @Size(min = 2, max = 100)
        String companyName,

        @NotBlank
        @Pattern(regexp = "\\d{9}$")
        String taxNumber,

        @NotBlank
        @Size(min = 2, max = 100)
        String customerName,

        @NotBlank
        @Email
        @Size(max = 255)
        String customerEmail,

        @NotBlank
        @Pattern(regexp = "^\\+994\\d{9}$")
        String customerPhone

) {}
