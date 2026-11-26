package br.ufla.inovatech.dto.usuario;

import br.ufla.inovatech.service.Sanitizer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String senha
) {
    public AuthRequest{
        email = Sanitizer.email(email);
    }
}
