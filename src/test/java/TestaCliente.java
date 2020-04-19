
import io.restassured.http.ContentType;

import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class TestaCliente {

    private String servicoCliente = "http://localhost:8080";
    private String recursoCliente = "/cliente";
    private String apagaTodosClientes = "/apagaTodos";

    @Test
    @DisplayName("Quando eu requisitar a lista de clientes sem adicionar clientes antes, Então ela deve estar vazia")
    public void quandoRequisitarListaClientesSemAdicionar_EntaoElaDeveEstarVazia() {
        apagaTodosClientesDoServidor();

        String respostaEsperada = "{}";

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(servicoCliente)
        .then()
            .statusCode(200)
            .assertThat().body(new IsEqual(respostaEsperada));
    }

    @Test
    @DisplayName("Quando eu cadastrar um cliente, Então ele deve ser salvo com sucesso")
    public void quandoCadastrarCliente_EntaoEleDeveSerSalvoComSucesso() {

        String corpoRequisicao = "{\n" +
                "  \"nome\": \"Vinny\",\n" +
                "  \"idade\": \"30\",\n" +
                "  \"id\": \"1234\"\n" +
                "}";

        String respostaEsperada = "{\"1234\":" +
                "{\"nome\":\"Vinny\"," +
                "\"idade\":30," +
                "\"id\":1234," +
                "\"risco\":0}" +
                "}";

        given()
            .contentType(ContentType.JSON)
            .body(corpoRequisicao)
        .when().
            post(servicoCliente+recursoCliente)
        .then()
            .statusCode(201)
            .assertThat().body(containsString(respostaEsperada));

        apagaTodosClientesDoServidor();
    }

    @Test
    @DisplayName("Quando eu atualizar um cliente, Então ele deve ser atualizado com sucesso")
    public void quandoAtualizarCliente_EntaoEleDeveSerAtualizadoComSucesso() {
        String corpoOriginalRequisicao = "{\n" +
                "  \"nome\": \"Vinny\",\n" +
                "  \"idade\": \"30\",\n" +
                "  \"id\": \"1234\"\n" +
                "}";

        String corpoAtualizadoRequisicao = "{\n" +
                "  \"nome\": \"Vinny Pessoni\",\n" +
                "  \"idade\": \"18\",\n" +
                "  \"id\": \"1234\"\n" +
                "}";

        String respostaEsperada = "{\"1234\":" +
                "{\"nome\":\"Vinny Pessoni\"," +
                "\"idade\":18," +
                "\"id\":1234," +
                "\"risco\":0}" +
                "}";
        given()
            .contentType(ContentType.JSON)
            .body(corpoOriginalRequisicao).
        when().
            post(servicoCliente+recursoCliente);


        given()
            .contentType(ContentType.JSON)
            .body(corpoAtualizadoRequisicao).
        when().
            put(servicoCliente+recursoCliente).
        then().
            statusCode(200).
            assertThat().body(containsString(respostaEsperada));

        apagaTodosClientesDoServidor();
    }


    @Test
    @DisplayName("Quando eu deletar um cliente, Então ele deve ser removido com sucesso")
    public void quandoDeletarCliente_EntaoEleDeveSerDeletadoComSucesso() {
        String corpoRequisicao = "{\n" +
                "  \"nome\": \"Vinny\",\n" +
                "  \"idade\": \"30\",\n" +
                "  \"id\": \"1234\"\n" +
                "}";

        String respostaEsperada = "CLIENTE REMOVIDO: { " +
                "NOME: Vinny, " +
                "IDADE: 30, " +
                "ID: 1234 }";

        given()
                .contentType(ContentType.JSON)
                .body(corpoRequisicao)
        .when().
                post(servicoCliente+recursoCliente);

        given()
                .contentType(ContentType.JSON)
        .when()
                .delete(servicoCliente+recursoCliente+"/1234")
        .then()
                .statusCode(200)
                .assertThat().body(new IsEqual(respostaEsperada));
    }


    /**
     * Método de apoio para apagar todos os clientes do servidor.
     * Usado para teste apenas.
     */
    public void apagaTodosClientesDoServidor(){
        String respostaEsperada = "{}";

        given()
            .contentType(ContentType.JSON)
        .when()
            .delete(servicoCliente+recursoCliente+apagaTodosClientes)
        .then()
            .statusCode(200)
            .assertThat().body(new IsEqual(respostaEsperada));
    }

}
