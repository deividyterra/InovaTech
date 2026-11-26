package br.ufla.inovatech.testeCaixaPreta;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
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

        RestAssured.baseURI = "https://gateway-sidagro-dev.dac.ufla.br";
        tempResponse = fillFormAuthAndGet(getPayload("login.json"),"/auth/login");
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
            data.setId(tempResponse.jsonPath().getInt("id"));
        }

        return tempResponse;
    }

    public static void cleanBefore(EntidadeTestes testEntity, String field, String content){
        int rows;
        String tableName = testEntity.getClass().getName().toLowerCase(Locale.ROOT);

        assertNotNull(tableName,"Erro ao coletar nome da entidade");

        rows = ((Number)DbUtils.buscarValor("SELECT COUNT(*) FROM public." + tableName + " WHERE " + field + " = ?", content)).intValue();

        if(rows > 0) DbUtils.executar("DELETE FROM public." + tableName + " WHERE " + field + " = ?", content);
    }

    @AfterEach
    public void cleanAfter() {
        Response tempResponse;
        String tableName;

        LOGGER.info("Executando a limpeza das entidades utilizadas no teste");

        for (EntidadeTestes e : cleanEntity) {
            tableName = e.getClass().getName();
            DbUtils.executar("DELETE FROM public." + tableName + " WHERE id" + " = ?", e.getId());
        }

        cleanEntity.clear();
    }

    public static File getPayload(String name){
        return new File(BaseTeste.class.getClassLoader().getResource("payload/" + name).getFile());
    }

}