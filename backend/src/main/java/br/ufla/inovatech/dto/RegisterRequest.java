package br.ufla.inovatech.dto;

import br.ufla.inovatech.service.Sanitizer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record RegisterRequest(
   @NotBlank
   @Size(max = 60 , min = 8)
   String nome,

   @NotBlank
   @Size(max = 30, min = 8)
   String nomeUsuario,

   @NotBlank
   @Email
   String email,

   @NotBlank
   @Size(max = 20,min = 8)
   @Pattern(
           regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*$",
           message = "A senha deve conter letra maiúscula, minúscula, número e caractere especial"
   )
   String senha,

   @NotBlank
   @CPF
   String cpf

) {
   public RegisterRequest {
      nome = Sanitizer.texto(nome);
      email = Sanitizer.email(email);
      cpf = Sanitizer.cpf(cpf);
   }
}