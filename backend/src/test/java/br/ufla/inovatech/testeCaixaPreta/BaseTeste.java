package br.ufla.inovatech.testeCaixaPreta;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public abstract class BaseTeste {

    private static final Logger LOGGER = Logger.getLogger(BaseTeste.class.getName());
    private static Set<EntidadeTestes> cleanEntity;

    @BeforeAll
    public static void setup(){
        Response tempResponse;

        LOGGER.info("\n******************************\nSetup inicial\n******************************");

        // URL da API do InovaTech. Para outra porta/host: mvn test -Dapi.url=http://localhost:8091
        RestAssured.baseURI = System.getProperty("api.url", "http://localhost:8080");
        tempResponse = fillFormAuthAndGet(getPayload("login.json"),"/usuario/login");
        assertEquals(200,tempResponse.getStatusCode(),"Usuário não foi logado");
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + tempResponse.jsonPath().getString("token"))
                .setContentType(ContentType.JSON)
                .build();
        cleanEntity = new LinkedHashSet<>();
    }

    public Response putAndGet(EntidadeTestes data, String path){
        Response tempResponse;

        tempResponse = given().contentType("application/json").body(data).when().put(path + "/" + data.getId()).then().extract().response();

        if(List.of(200,201,204).contains(tempResponse.statusCode())){
            cleanEntity.add(data);
        }
        return tempResponse;
    }

    public Response delete(EntidadeTestes data, String path){
        Response tempResponse;

        assertNotNull(data.getId(), "A entidade precisa de id para ser removida");

        tempResponse = given().when().delete(path + "/" + data.getId()).then().extract().response();
        assertNotNull(tempResponse, "Não foi possível remover a entidade");

        if(List.of(200,204).contains(tempResponse.statusCode())){
            // Compara por tipo + id (e não pelo objeto), cobrindo cópias da mesma entidade.
            cleanEntity.removeIf(e -> e.getClass().equals(data.getClass()) && data.getId().equals(e.getId()));
        }
        return tempResponse;
    }

    public static Response fillFormAuthAndGet(File data, String path){
        Response tempResponse;

        tempResponse = given().contentType("application/json").body(data).when().post(path).then().extract().response();
        assertNotNull(tempResponse,"Não foi autenticar");
        return tempResponse;
    }

    public static Response fillFormAndGet(EntidadeTestes data, String path){
        Response tempResponse;

        tempResponse = given().contentType("application/json").body(data).when().post(path).then().extract().response();
        assertNotNull(tempResponse,"Não foi possível criar a entidade");

        if(List.of(200,201,204).contains(tempResponse.statusCode())){
            cleanEntity.add(data);
            // Nem toda resposta traz "id" (ex.: /cadastro e /login devolvem só o token).
            Object id = tempResponse.jsonPath().get("id");
            if (id instanceof Number numero) {
                data.setId(numero.intValue());
            }
        }

        return tempResponse;
    }

    public static void cleanBefore(EntidadeTestes testEntity, String field, String content){
        int rows;
        String tableName = testEntity.tabela();

        assertNotNull(tableName,"Erro ao coletar nome da entidade");

        rows = ((Number)DbUtils.buscarValor("SELECT COUNT(*) FROM public." + tableName + " WHERE " + field + " = ?", content)).intValue();

        if(rows > 0) {
            LOGGER.info("Removendo " + rows + " registro(s) de " + tableName + " com " + field + " = " + content);
            DbUtils.executar("DELETE FROM public." + tableName + " WHERE " + field + " = ?", content);
        }
    }

    @AfterEach
    public void cleanAfter() {
        LOGGER.info("Executando a limpeza das entidades utilizadas no teste");

        for (EntidadeTestes e : cleanEntity) {
            if (e.getId() == null) {
                LOGGER.warning("Entidade de " + e.tabela() + " sem id, não foi possível removê-la automaticamente."
                        + " Use cleanBefore com um campo único (ex.: cpf).");
                continue;
            }
            DbUtils.executar("DELETE FROM public." + e.tabela() + " WHERE " + e.colunaId() + " = ?", e.getId());
        }

        cleanEntity.clear();
    }

    public static File getPayload(String name){
        String caminho = "payload/" + name;

        URL url = BaseTeste.class.getClassLoader().getResource(caminho);
        if (url != null) {
            try {
                return Paths.get(url.toURI()).toFile();
            } catch (URISyntaxException e) {
                throw new IllegalStateException("Caminho inválido para o payload: " + url, e);
            }
        }

        Path origem = Paths.get("src", "test", "resources", caminho);
        if (Files.exists(origem)) {
            return origem.toFile();
        }

        throw new IllegalArgumentException("Payload não encontrado: " + caminho
                + " (procurado no classpath e em " + origem.toAbsolutePath() + ")");
    }

}