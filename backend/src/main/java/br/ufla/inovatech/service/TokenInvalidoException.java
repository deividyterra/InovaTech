package br.ufla.inovatech.service;

/**
 * Lançada quando o token está ausente, mal formado, com assinatura inválida ou expirado.
 */
public class TokenInvalidoException extends RuntimeException {

    public TokenInvalidoException(String mensagem) {
        super(mensagem);
    }

    public TokenInvalidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
