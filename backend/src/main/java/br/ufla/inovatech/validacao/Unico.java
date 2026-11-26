package br.ufla.inovatech.validacao;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Garante que o valor do campo ainda não existe no banco para a entidade informada.
 * Em rotas com {id} na URL (ex.: PUT /usuario/{id}), o próprio registro é ignorado,
 * permitindo salvar sem trocar o valor.
 */
@Documented
@Constraint(validatedBy = UnicoValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Unico {

    String message() default "Valor já cadastrado";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** Entidade JPA onde o valor será procurado. */
    Class<?> entidade();

    /** Nome do atributo na entidade (nome do campo Java, não da coluna). */
    String campo();

    /** Nome do atributo de id na entidade. */
    String campoId() default "id";

    /** Nome da variável de caminho que contém o id do registro sendo editado. */
    String variavelCaminho() default "id";
}
