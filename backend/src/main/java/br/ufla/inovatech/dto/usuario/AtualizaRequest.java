package br.ufla.inovatech.dto.usuario;

import br.ufla.inovatech.service.Sanitizer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record AtualizaRequest(
        @NotBlank
        @Size(max = 60, min = 8)
        String nome,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @CPF
        String cpf
) {
    public AtualizaRequest {
        nome = Sanitizer.texto(nome);
        email = Sanitizer.email(email);
        cpf = Sanitizer.cpf(cpf);
    }
}
