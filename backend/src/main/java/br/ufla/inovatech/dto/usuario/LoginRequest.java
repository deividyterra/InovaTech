package br.ufla.inovatech.dto.usuario;

import br.ufla.inovatech.service.Sanitizer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String senha
) {
    public LoginRequest{
        email = Sanitizer.email(email);
    }
}
