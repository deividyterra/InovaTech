package br.ufla.inovatech.service;

import br.ufla.inovatech.dto.UsuarioTokenDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private static final String SECRET = "chave-de-teste-com-pelo-menos-32-caracteres";
    private static final String EMAIL = "maria@inovatech.com";
    private static final String CPF = "52998224725";

    private final TokenService service = new TokenService(SECRET, 60);

    @Test
    void geraTokenERecuperaEmailCpfEPerfil() {
        String token = service.gerarToken(EMAIL, CPF, "GARCOM");

        UsuarioTokenDTO usuario = service.extrairUsuario(token);

        assertEquals(EMAIL, usuario.email());
        assertEquals(CPF, usuario.cpf());
        assertEquals("GARCOM", usuario.perfil());
        assertEquals(EMAIL, service.extrairEmail(token));
        assertEquals(CPF, service.extrairCpf(token));
        assertEquals("GARCOM", service.extrairPerfil(token));
    }

    @Test
    void extraiPerfilDeAdministrador() {
        String token = service.gerarToken("admin@inovatech.com", "00000000000", "ADMINISTRADOR");

        assertEquals("ADMINISTRADOR", service.extrairPerfil(token));
    }

    @Test
    void aceitaTokenComPrefixoBearer() {
        String token = service.gerarToken(EMAIL, CPF, "ADMINISTRADOR");

        assertEquals("ADMINISTRADOR", service.extrairPerfil("Bearer " + token));
    }

    @Test
    void naoGeraTokenSemPerfil() {
        assertThrows(IllegalArgumentException.class, () -> service.gerarToken(EMAIL, CPF, null));
        assertThrows(IllegalArgumentException.class, () -> service.gerarToken(EMAIL, CPF, " "));
    }

    @Test
    void rejeitaTokenAlterado() {
        String token = service.gerarToken(EMAIL, CPF, "GARCOM");
        String adulterado = token.substring(0, token.length() - 2) + "xx";

        assertThrows(TokenInvalidoException.class, () -> service.extrairUsuario(adulterado));
    }

    @Test
    void rejeitaTokenAssinadoComOutraChave() {
        TokenService outro = new TokenService("outra-chave-diferente-com-mais-de-32-chars", 60);
        String token = outro.gerarToken(EMAIL, CPF, "ADMINISTRADOR");

        assertThrows(TokenInvalidoException.class, () -> service.extrairPerfil(token));
    }

    @Test
    void rejeitaTokenExpirado() {
        TokenService expiraNaHora = new TokenService(SECRET, -1);
        String token = expiraNaHora.gerarToken(EMAIL, CPF, "GARCOM");

        assertThrows(TokenInvalidoException.class, () -> service.extrairUsuario(token));
    }

    @Test
    void rejeitaTokenVazioOuMalFormado() {
        assertThrows(TokenInvalidoException.class, () -> service.extrairUsuario(null));
        assertThrows(TokenInvalidoException.class, () -> service.extrairUsuario("  "));
        assertThrows(TokenInvalidoException.class, () -> service.extrairUsuario("abc.def"));
        assertThrows(TokenInvalidoException.class, () -> service.extrairUsuario("a.b<script>.c"));
    }
}
