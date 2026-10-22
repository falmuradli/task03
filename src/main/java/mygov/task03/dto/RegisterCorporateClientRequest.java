package mygov.task03.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import mygov.task03.model.Company;

import java.text.Format;

public record RegisterCorporateClientRequest(
        @NotBlank
        @Size(min = 2, max = 100)
        String companyName,

        @NotBlank
        String taxNumber,

        @NotBlank
        @Size(min = 2, max = 100)
        String customerName,

        @Email
        String customerEmail,

        @Pattern(regexp = "^\\+994\\d{9}$")
        String customerPhone

) {
}
