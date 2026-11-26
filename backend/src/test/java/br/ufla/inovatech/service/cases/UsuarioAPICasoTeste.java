package br.ufla.inovatech.service.cases;

import br.ufla.inovatech.service.entity.Usuario;
import br.ufla.inovatech.testeCaixaPreta.BaseTeste;
import br.ufla.inovatech.testeCaixaPreta.DbUtils;
import io.restassured.RestAssured;
import io.restassured.config.HeaderConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UsuarioAPICasoTeste extends BaseTeste{

    private static final Logger LOGGER = Logger.getLogger(UsuarioAPICasoTeste.class.getName());
    private static final String caminho = "/usuario";

    public void limpar(Usuario usuario){
        cleanBefore(usuario,"cpf",usuario.getCpf());
        cleanBefore(usuario,"login_usuario",usuario.getNomeUsuario());
        cleanBefore(usuario,"email", usuario.getEmail());
    }

    @Test
    public void usuarioFluxoSimples(){
        Response response;
        Usuario usuario;

        LOGGER.info("efetua o registro de um novo usuário");
        usuario = new Usuario(getPayload("cadastro.json"));
        limpar(usuario);
        response = fillFormAndGet(usuario,caminho + "/cadastro");
        assertEquals(200,response.statusCode(),"Não foi possivel realizar o cadastro do usuário");

        Object id = DbUtils.buscarValor("SELECT " + usuario.colunaId() + " FROM public." + usuario.tabela()
                + " WHERE cpf = ?", usuario.getCpf());
        assertNotNull(id, "Usuário cadastrado não encontrado no banco");
        usuario.setId(((Number) id).intValue());

        LOGGER.info("efetua o login e valida a resposta");
        response = fillFormAndGet(usuario,caminho + "/login");
        assertEquals(200,response.statusCode(),"Não foi possível efetuar o login com a entidade criada");

        LOGGER.info("efetua as alterações e aguarda o sucesso da operação");
        usuario.setCpf("01234567890");
        usuario.setEmail("TesteEdicao@123.com");
        usuario.setNome("Usuario Editado"); // nome exige no mínimo 8 caracteres
        // Limpa só os campos alterados: limpar() também apagaria pelo login, que é o do próprio usuário.
        cleanBefore(usuario,"cpf",usuario.getCpf());
        cleanBefore(usuario,"email",usuario.getEmail());
        response = putAndGet(usuario,caminho); // putAndGet já concatena "/" + id
        assertEquals(200,response.statusCode(),"Não foi possível alterar a entidade");

        LOGGER.info("efetua a remoção do usuário do sistema e aguarda o sucesso");
        response = delete(usuario,caminho);
        assertEquals(204,response.statusCode(),"Não foi possível apagar a entidade");
    }

    private Integer buscarId(Usuario usuario){
        Object id = DbUtils.buscarValor("SELECT " + usuario.colunaId() + " FROM public." + usuario.tabela()
                + " WHERE cpf = ?", usuario.getCpf());
        assertNotNull(id, "Usuário cadastrado não encontrado no banco");
        usuario.setId(((Number) id).intValue());
        return usuario.getId();
    }

    private Response alterarSenha(Integer id, String token, String senhaAtual, String novaSenha, String confirmarSenha){
        Map<String, String> corpo = new HashMap<>();
        corpo.put("senhaAtual", senhaAtual);
        corpo.put("novaSenha", novaSenha);
        corpo.put("confirmarSenha", confirmarSenha);

        RequestSpecification requisicao = given().config(RestAssured.config()
                .headerConfig(HeaderConfig.headerConfig().overwriteHeadersWithName("Authorization")));
        if (token != null) {
            requisicao.header("Authorization", "Bearer " + token);
        }
        return requisicao.contentType("application/json").body(corpo)
                .when().put(caminho + "/" + id + "/senha")
                .then().extract().response();
    }

    @Test
    public void alterarSenha(){
        Response response;
        Usuario usuario;
        Usuario outroUsuario;
        String senhaOriginal;
        String token;

        LOGGER.info("Cria as entidades necessárias para o teste");
        usuario = new Usuario(getPayload("cadastro.json"));
        limpar(usuario);
        senhaOriginal = usuario.getSenha();
        response = fillFormAndGet(usuario, caminho + "/cadastro");
        assertEquals(200, response.statusCode(), "Não foi possível criar a entidade necessária para o teste");
        token = response.jsonPath().getString("token");
        buscarId(usuario);

        outroUsuario = new Usuario(getPayload("cadastro.json"));
        outroUsuario.setCpf("03842539622");
        outroUsuario.setEmail("outro.usuario@inovatech.com");
        outroUsuario.setNomeUsuario("outrousuario");
        limpar(outroUsuario);
        response = fillFormAndGet(outroUsuario, caminho + "/cadastro");
        assertEquals(200, response.statusCode(), "Não foi possível criar a segunda entidade necessária para o teste");
        buscarId(outroUsuario);

        LOGGER.info("Tenta alterar a senha sem token e aguarda o erro");
        response = alterarSenha(usuario.getId(), "", senhaOriginal, "NovaSenha@1", "NovaSenha@1");
        assertEquals(401, response.statusCode(), "Senha alterada sem autenticação");

        LOGGER.info("Tenta alterar a senha informando a senha atual incorreta");
        response = alterarSenha(usuario.getId(), token, "Errada@123", "NovaSenha@1", "NovaSenha@1");
        assertEquals(400, response.statusCode(), "Senha alterada com a senha atual incorreta");

        LOGGER.info("Tenta alterar a senha sem informar a senha atual");
        response = alterarSenha(usuario.getId(), token, null, "NovaSenha@1", "NovaSenha@1");
        assertEquals(400, response.statusCode(), "Usuário comum alterou a senha sem informar a senha atual");

        LOGGER.info("Tenta alterar a senha com a confirmação diferente da nova senha");
        response = alterarSenha(usuario.getId(), token, senhaOriginal, "NovaSenha@1", "OutraSenha@1");
        assertEquals(400, response.statusCode(), "Senha alterada com a confirmação diferente");

        LOGGER.info("Tenta alterar para uma senha fraca");
        response = alterarSenha(usuario.getId(), token, senhaOriginal, "fraca", "fraca");
        assertEquals(400, response.statusCode(), "Senha alterada para uma senha fraca");

        LOGGER.info("Tenta alterar a senha de outro usuário sem ser administrador");
        response = alterarSenha(outroUsuario.getId(), token, outroUsuario.getSenha(), "NovaSenha@1", "NovaSenha@1");
        assertEquals(403, response.statusCode(), "Usuário comum alterou a senha de outro usuário");

        LOGGER.info("Garante que as tentativas inválidas não alteraram a senha");
        response = fillFormAndGet(usuario, caminho + "/login");
        assertEquals(200, response.statusCode(), "A senha original deixou de funcionar após tentativas inválidas");

        LOGGER.info("Altera a própria senha corretamente e aguarda o sucesso");
        response = alterarSenha(usuario.getId(), token, senhaOriginal, "NovaSenha@1", "NovaSenha@1");
        assertEquals(204, response.statusCode(), "Não foi possível alterar a própria senha");

        LOGGER.info("Valida que a senha antiga não funciona mais e a nova sim");
        response = fillFormAndGet(usuario, caminho + "/login");
        assertEquals(401, response.statusCode(), "A senha antiga continua funcionando após a alteração");
        usuario.setSenha("NovaSenha@1");
        response = fillFormAndGet(usuario, caminho + "/login");
        assertEquals(200, response.statusCode(), "Não foi possível efetuar login com a nova senha");

        LOGGER.info("Administrador altera a senha de outro usuário sem informar a senha atual");
        response = alterarSenha(usuario.getId(), null, null, "SenhaAdmin@1", "SenhaAdmin@1");
        assertEquals(204, response.statusCode(), "Administrador não conseguiu alterar a senha do usuário");
        usuario.setSenha("SenhaAdmin@1");
        response = fillFormAndGet(usuario, caminho + "/login");
        assertEquals(200, response.statusCode(), "Não foi possível efetuar login com a senha definida pelo administrador");

        LOGGER.info("Tenta alterar a senha de um usuário inexistente e aguarda o erro");
        response = alterarSenha(Integer.MAX_VALUE, null, null, "NovaSenha@1", "NovaSenha@1");
        assertEquals(404, response.statusCode(), "Senha alterada para um usuário inexistente");
    }

    @Test
    public void cadastroCamposObrigatorios(){
        Response response;
        Usuario usuario;

        LOGGER.info("Tenta efetuar o cadastro de um usuário faltando o email");
        usuario = new Usuario(getPayload("cadastro.json"));
        limpar(usuario);
        usuario.setEmail(null);
        response = fillFormAndGet(usuario,caminho + "/cadastro");
        assertEquals(400,response.statusCode(),"Erro na validação de obrigatoriedade do campo email");

        LOGGER.info("Tenta efetuar o cadastro de um usuário faltando o senha");
        usuario = new Usuario(getPayload("cadastro.json"));
        usuario.setSenha(null);
        response = fillFormAndGet(usuario,caminho + "/cadastro");
        assertEquals(400,response.statusCode(),"Erro na validação de obrigatoriedade do campo senha");

        LOGGER.info("Tenta efetuar o cadastro de um usuário faltando o nome de usuário");
        usuario = new Usuario(getPayload("cadastro.json"));
        usuario.setNomeUsuario(null);
        response = fillFormAndGet(usuario,caminho + "/cadastro");
        assertEquals(400,response.statusCode(),"Erro na validação de obrigatoriedade do campo nome de usuário");

        LOGGER.info("Tenta efetuar o cadastro de um usuário faltando o cpf");
        usuario = new Usuario(getPayload("cadastro.json"));
        usuario.setCpf(null);
        response = fillFormAndGet(usuario,caminho + "/cadastro");
        assertEquals(400,response.statusCode(),"Erro na validação de obrigatoriedade do campo cpf");
    }

    @Test
    public void cadastroSenhaFraca(){
        Response response;
        Usuario usuario;

        LOGGER.info("Tenta efetuar o cadastro utilizando uma senha fraca");
        usuario = new Usuario(getPayload("cadastro.json"));
        limpar(usuario);
        usuario.setSenha("123");
        response = fillFormAndGet(usuario,caminho + "/cadastro");
        assertEquals(400,response.statusCode(),"Erro na validação de senha fraca");
    }

    @Test
    public void loginCredenciaisIncorretas(){
        Response response;
        Usuario usuario;

        LOGGER.info("Cria as entidades necessárias para o teste");
        usuario = new Usuario(getPayload("cadastro.json"));
        limpar(usuario);
        response = fillFormAndGet(usuario,caminho + "/cadastro");
        assertEquals(200,response.statusCode(),"Não foi possível criar a entidade necessária para o teste");

        LOGGER.info("Tenta efetuar o login com a senha incorreta");
        usuario.setSenha("12345678Aa@");
        response = fillFormAndGet(usuario, caminho + "/login");
        assertEquals(401,response.statusCode(),"Erro na validação de senha incorreta");

        LOGGER.info("Tenta efetuar o login com o nome de usuário incorreto");
        usuario = new Usuario(getPayload("cadastro.json"));
        usuario.setNomeUsuario("joao123");
        response = fillFormAndGet(usuario,caminho + "/login");

    }

    @Test
    public void cadastroUnicidadeCpf(){
        Response response;
        Usuario usuario;

        LOGGER.info("cadastra as entidades necessárias para o teste");
        usuario = new Usuario(getPayload("cadastro.json"));
        limpar(usuario);
        response = fillFormAndGet(usuario,caminho + "/cadastro");
        assertEquals(200,response.statusCode(),"Não foi possível cadastrar a entidade necessária para o teste");

        LOGGER.info("Tenta cadastrar outra entidade com o mesmo cpf e aguarda o erro");
        usuario.setEmail("Teste123@gmail.com");
        usuario.setNomeUsuario("Joãozinho1234");
        cleanBefore(usuario,"login_usuario",usuario.getNomeUsuario());
        cleanBefore(usuario,"email",usuario.getEmail());
        response = fillFormAndGet(usuario,caminho + "/cadastro");
        assertEquals(400,response.statusCode(),"Erro na validação de duplicidade de cpf");

    }

    @Test
    public void cadastroUnicidadeNomeUsuario(){
        Response response;
        Usuario usuario;

        LOGGER.info("cadastra as entidades necessárias para o teste");
        usuario = new Usuario(getPayload("cadastro.json"));
        limpar(usuario);
        response = fillFormAndGet(usuario,caminho + "/cadastro");
        assertEquals(200,response.statusCode(),"Não foi possível cadastrar a entidade necessária para o teste");

        LOGGER.info("Tenta cadastrar outra entidade com o mesmo nome de usuário e aguarda o erro");
        usuario.setEmail("Teste123@gmail.com");
        usuario.setCpf("03842539622");
        cleanBefore(usuario,"cpf",usuario.getCpf());
        cleanBefore(usuario,"email",usuario.getEmail());
        response = fillFormAndGet(usuario,caminho + "/cadastro");
        assertEquals(400,response.statusCode(),"Erro na validação de duplicidade de nome de usuário");
    }

    @Test
    public void cadastroUnicidadeEmail(){
        Response response;
        Usuario usuario;

        LOGGER.info("cadastra as entidades necessárias para o teste");
        usuario = new Usuario(getPayload("cadastro.json"));
        limpar(usuario);
        response = fillFormAndGet(usuario,caminho + "/cadastro");
        assertEquals(200,response.statusCode(),"Não foi possível cadastrar a entidade necessária para o teste");

        LOGGER.info("Tenta cadastrar outra entidade com o mesmo email e aguarda o erro");
        usuario.setCpf("03842539622");
        usuario.setNomeUsuario("Joãozinho1234");
        cleanBefore(usuario,"cpf",usuario.getCpf());
        cleanBefore(usuario,"login_usuario",usuario.getNomeUsuario());
        response = fillFormAndGet(usuario,caminho + "/cadastro");
        assertEquals(400,response.statusCode(),"Erro na validação de duplicidade de nome de usuário");
    }

}
