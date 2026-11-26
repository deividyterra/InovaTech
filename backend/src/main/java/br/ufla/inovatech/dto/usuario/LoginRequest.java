package br.ufla.inovatech.dto.usuario;

import br.ufla.inovatech.service.Sanitizer;
import jakarta.validation.constraints.NotBlank;

/** Login por Nome de Usuário e senha (RF-001). */
public record LoginRequest(
        @NotBlank
        String nomeUsuario,

        @NotBlank
        String senha
) {
    public LoginRequest{
        nomeUsuario = Sanitizer.texto(nomeUsuario);
    }
}
