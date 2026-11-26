package br.ufla.inovatech.validacao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

public class UnicoValidator implements ConstraintValidator<Unico, Object> {

    @PersistenceContext
    private EntityManager entityManager;

    private Unico anotacao;

    @Override
    public void initialize(Unico anotacao) {
        this.anotacao = anotacao;
    }

    @Override
    public boolean isValid(Object valor, ConstraintValidatorContext context) {
        // Valor vazio é responsabilidade do @NotBlank / @NotNull.
        if (valor == null || (valor instanceof String s && s.isBlank())) {
            return true;
        }

        String entidade = anotacao.entidade().getSimpleName();
        Integer idAtual = idDaRequisicao();

        String jpql = "SELECT COUNT(e) FROM " + entidade + " e WHERE e." + anotacao.campo() + " = :valor"
                + (idAtual != null ? " AND e." + anotacao.campoId() + " <> :id" : "");

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("valor", valor);
        if (idAtual != null) {
            query.setParameter("id", idAtual);
        }

        return query.getSingleResult() == 0;
    }

    /** Lê o {id} da URL da requisição atual (null no cadastro, que não tem id). */
    @SuppressWarnings("unchecked")
    private Integer idDaRequisicao() {
        RequestAttributes atributos = RequestContextHolder.getRequestAttributes();
        if (atributos == null) return null;

        Object variaveis = atributos.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (!(variaveis instanceof Map<?, ?> mapa)) return null;

        Object id = ((Map<String, String>) mapa).get(anotacao.variavelCaminho());
        try {
            return id == null ? null : Integer.valueOf(id.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
