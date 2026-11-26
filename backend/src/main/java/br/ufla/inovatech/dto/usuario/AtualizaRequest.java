package br.ufla.inovatech.dto.usuario;

import br.ufla.inovatech.model.Usuario;
import br.ufla.inovatech.validacao.Unico;
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
        @Unico(entidade = Usuario.class, campo = "email", message = "E-mail já cadastrado")
        String email,

        @NotBlank
        @CPF
        @Unico(entidade = Usuario.class, campo = "cpf", message = "CPF já cadastrado")
        String cpf
) {
    public AtualizaRequest {
        nome = Sanitizer.texto(nome);
        email = Sanitizer.email(email);
        cpf = Sanitizer.cpf(cpf);
    }
}
