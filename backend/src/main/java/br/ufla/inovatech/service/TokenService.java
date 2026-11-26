package br.ufla.inovatech.service;

import br.ufla.inovatech.dto.UsuarioTokenDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class TokenService {

    private static final String ISSUER = "inovatech";
    private static final String CLAIM_CPF = "cpf";
    private static final String CLAIM_PERFIL = "perfil";

    private final SecretKey chave;
    private final Duration expiracao;

    public TokenService(
            @Value("${jwt.secret}") String chaveSecreta,
            @Value("${jwt.expiracao-minutos}") long expiracaoMinutos) {

        byte[] bytes = chaveSecreta.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("jwt.secret deve ter pelo menos 32 caracteres (256 bits).");
        }
        this.chave = Keys.hmacShaKeyFor(bytes);
        this.expiracao = Duration.ofMinutes(expiracaoMinutos);
    }

    /**
     * Gera um token assinado com o e-mail (subject), o CPF e o perfil do usuário.
     */
    public String gerarToken(String email, String cpf, String perfil) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O e-mail do usuário é obrigatório.");
        }
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("O CPF do usuário é obrigatório.");
        }
        if (perfil == null || perfil.isBlank()) {
            throw new IllegalArgumentException("O perfil do usuário é obrigatório.");
        }

        Instant agora = Instant.now();

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(email)
                .claim(CLAIM_CPF, cpf)
                .claim(CLAIM_PERFIL, perfil)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(expiracao)))
                .signWith(chave)
                .compact();
    }

    public UsuarioTokenDTO extrairUsuario(String token) {
        Claims claims = validar(token);

        String email = claims.getSubject();
        String cpf = claims.get(CLAIM_CPF, String.class);
        String perfil = claims.get(CLAIM_PERFIL, String.class);

        if (email == null || email.isBlank() || cpf == null || cpf.isBlank()
                || perfil == null || perfil.isBlank()) {
            throw new TokenInvalidoException("Token não contém e-mail, CPF ou perfil válidos.");
        }

        return new UsuarioTokenDTO(cpf, email, perfil);
    }

    /** Atalho para obter só o e-mail do usuário a partir do token. */
    public String extrairEmail(String token) {
        return extrairUsuario(token).email();
    }

    /** Atalho para obter só o CPF do usuário a partir do token. */
    public String extrairCpf(String token) {
        return extrairUsuario(token).cpf();
    }

    /** Atalho para obter só o perfil do usuário a partir do token (ex.: "ADMINISTRADOR"). */
    public String extrairPerfil(String token) {
        return extrairUsuario(token).perfil();
    }

    private Claims validar(String token) {
        if (token == null || token.isBlank()) {
            throw new TokenInvalidoException("Token não informado.");
        }

        String jwt = Sanitizer.token(token);
        if (jwt == null) {
            throw new TokenInvalidoException("Token mal formado.");
        }

        try {
            return Jwts.parser()
                    .verifyWith(chave)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenInvalidoException("Token inválido ou expirado.", e);
        }
    }

    public boolean tokenValido(String token) {
        try {
            validar(token);
            return true;
        } catch (TokenInvalidoException e) {
            return false;
        }
    }
}