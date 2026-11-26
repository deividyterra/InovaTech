package br.ufla.inovatech.dto.usuario;

import br.ufla.inovatech.model.Usuario;
import br.ufla.inovatech.validacao.Unico;
import br.ufla.inovatech.service.Sanitizer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record CadastroRequest(
   @NotBlank
   @Size(max = 60 , min = 8)
   String nome,

   @NotBlank
   @Size(max = 30, min = 8)
   @Unico(entidade = Usuario.class, campo = "nomeUsuario", message = "Nome de usuário já está em uso")
   String nomeUsuario,

   @NotBlank
   @Email
   @Unico(entidade = Usuario.class, campo = "email", message = "E-mail já cadastrado")
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
   @Unico(entidade = Usuario.class, campo = "cpf", message = "CPF já cadastrado")
   String cpf,

   @Pattern(
           regexp = "^(GARCOM|CHEF_COZINHA|GESTOR_PEDIDOS|CAIXA|ADMINISTRADOR)$",
           message = "Perfil inválido"
   )
   String perfil
) {
   public CadastroRequest {
      nome = Sanitizer.texto(nome);
      email = Sanitizer.email(email);
      cpf = Sanitizer.cpf(cpf);
      perfil = (perfil == null || perfil.isBlank()) ? "GARCOM" : perfil.trim().toUpperCase();
   }
}