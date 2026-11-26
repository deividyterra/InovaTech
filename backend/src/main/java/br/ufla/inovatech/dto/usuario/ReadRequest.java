package br.ufla.inovatech.dto.usuario;

import br.ufla.inovatech.service.Sanitizer;
import jakarta.validation.constraints.NotBlank;

public record ReadRequest(
        @NotBlank
        String token
) {
    public ReadRequest{
        token = Sanitizer.token(token);
    }
}
