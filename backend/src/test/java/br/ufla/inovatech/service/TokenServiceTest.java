package br.ufla.inovatech.service;

import br.ufla.inovatech.dto.UsuarioTokenDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private static final String SECRET = "chave-de-teste-com-pelo-menos-32-caracteres";

    private final TokenService service = new TokenService(SECRET, 60);

    @Test
    void geraTokenERecuperaIdENome() {
        String token = service.gerarToken(7, "Maria Silva");

        UsuarioTokenDTO usuario = service.extrairUsuario(token);

        assertEquals(7, usuario.id());
        assertEquals("Maria Silva", usuario.nome());
        assertEquals(7, service.extrairId(token));
        assertEquals("Maria Silva", service.extrairNome(token));
    }

    @Test
    void aceitaTokenComPrefixoBearer() {
        String token = service.gerarToken(1, "Administrador");

        assertEquals(1, service.extrairId("Bearer " + token));
    }

    @Test
    void rejeitaTokenAlterado() {
        String token = service.gerarToken(1, "Administrador");
        String adulterado = token.substring(0, token.length() - 2) + "xx";

        assertThrows(TokenInvalidoException.class, () -> service.extrairUsuario(adulterado));
    }

    @Test
    void rejeitaTokenAssinadoComOutraChave() {
        TokenService outro = new TokenService("outra-chave-diferente-com-mais-de-32-chars", 60);
        String token = outro.gerarToken(1, "Administrador");

        assertThrows(TokenInvalidoException.class, () -> service.extrairUsuario(token));
    }

    @Test
    void rejeitaTokenExpirado() {
        TokenService expiraNaHora = new TokenService(SECRET, -1);
        String token = expiraNaHora.gerarToken(1, "Administrador");

        assertThrows(TokenInvalidoException.class, () -> service.extrairUsuario(token));
    }

    @Test
    void rejeitaTokenVazio() {
        assertThrows(TokenInvalidoException.class, () -> service.extrairUsuario(null));
        assertThrows(TokenInvalidoException.class, () -> service.extrairUsuario("  "));
    }
}
