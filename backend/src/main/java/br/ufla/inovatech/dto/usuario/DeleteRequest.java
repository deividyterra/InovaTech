package br.ufla.inovatech.dto.usuario;

import br.ufla.inovatech.service.Sanitizer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeleteRequest(
        @NotBlank
        String token,

        @NotNull
        Integer id
) {
    public DeleteRequest {
        token = Sanitizer.token(token);
    }
}
