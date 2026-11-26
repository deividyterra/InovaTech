package br.ufla.inovatech.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record AlterarSenhaRequest(
        String senhaAtual,

        @NotBlank
        @Size(max = 20, min = 8)
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*$",
                message = "A senha deve conter letra maiúscula, minúscula, número e caractere especial"
        )
        String novaSenha,

        @NotBlank
        String confirmarSenha
) {
}
