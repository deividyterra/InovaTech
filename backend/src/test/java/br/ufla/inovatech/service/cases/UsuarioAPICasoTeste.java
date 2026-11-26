package br.ufla.inovatech.service.cases;

import br.ufla.inovatech.service.entity.Usuario;
import br.ufla.inovatech.testeCaixaPreta.BaseTeste;
import br.ufla.inovatech.testeCaixaPreta.DbUtils;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UsuarioAPICasoTeste extends BaseTeste{

    private static final Logger LOGGER = Logger.getLogger(UsuarioAPICasoTeste.class.getName());
    private static final String caminho = "/usuario";

    @Test
    public void usuarioFluxoSimples(){
        Response response;
        Usuario usuario;

        LOGGER.info("efetua o registro de um novo usuário");
        usuario = new Usuario(getPayload("cadastro.json"));
        cleanBefore(usuario,"cpf", usuario.getCpf());
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
        cleanBefore(usuario,"cpf", usuario.getCpf());
        response = putAndGet(usuario,caminho); // putAndGet já concatena "/" + id
        assertEquals(200,response.statusCode(),"Não foi possível alterar a entidade");

        LOGGER.info("efetua a remoção do usuário do sistema e aguarda o sucesso");
        response = delete(usuario,caminho);
        assertEquals(204,response.statusCode(),"Não foi possível apagar a entidade");
    }

    @Test
    public void alterarSenha(){

    }

    @Test
    public void cadastroCamposObrigatorios(){
        LOGGER.info("Tenta efetuar o cadastro de um usuário faltando o email");
        LOGGER.info("Tenta efetuar o cadastro de um usuário faltando o senha");
        LOGGER.info("Tenta efetuar o cadastro de um usuário faltando o nome de usuário");
        LOGGER.info("Tenta efetuar o cadastro de um usuário faltando o cpf");
    }

    @Test
    public void cadastroSenhaFraca(){
        LOGGER.info("Tenta efetuar o cadastro utilizando uma senha fraca");
    }

    @Test
    public void loginCredenciaisIncorretas(){
        LOGGER.info("Cria as entidades necessárias para o teste");
        LOGGER.info("Tenta efetuar o login com a senha incorreta");
        LOGGER.info("Tenta efetuar o login com o nome de usuário incorreto");
    }

    @Test
    public void cadastroUnicidadeCpf(){
        LOGGER.info("cadastra as entidades necessárias para o teste");
        LOGGER.info("Tenta cadastrar outra entidade com o mesmo cpf e aguarda o erro");
    }

    @Test
    public void cadastroUnicidadeNomeUsuario(){
        LOGGER.info("cadastra as entidades necessárias para o teste");
        LOGGER.info("Tenta cadastrar outra entidade com o mesmo nome de usuário e aguarda o erro");
    }

    @Test
    public void cadastroUnicidadeEmail(){
        LOGGER.info("cadastra as entidades necessárias para o teste");
        LOGGER.info("Tenta cadastrar outra entidade com o mesmo email e aguarda o erro");
    }

}
