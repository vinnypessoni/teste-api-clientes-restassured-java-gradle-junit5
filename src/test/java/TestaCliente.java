
import io.restassured.http.ContentType;

import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;



public class TestaCliente {

    private String servicoCliente = "http://localhost:8080";
    private String recursoCliente = "/cliente";
    private String apagaTodosClientes = "/apagaTodos";

    @Test
    @DisplayName("Quando eu requisitar a lista de clientes sem adicionar clientes antes, Então ela deve estar vazia")
    public void quandoRequisitarListaClientesSemAdicionar_EntaoElaDeveEstarVazia() {

        /**
         * Observe que aqui chamamos o método de apoio para apagar todos os clientes do servidor como forma de preparar
         * o servidor para esse teste que tem como pré condição ele estar vazio.
         */
        apagaTodosClientesDoServidor();

        String respostaEsperada = "{}";

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(servicoCliente)
        .then()
            .statusCode(200)
            .body(equalTo(respostaEsperada));
    }

    @Test
    @DisplayName("Quando eu cadastrar um cliente, Então ele deve ser salvo com sucesso - Forma 1 de ser fazer")
    public void quandoCadastrarCliente_EntaoEleDeveSerSalvoComSucesso() {
        apagaTodosClientesDoServidor();

        String respostaEsperada = "{\"10201\":{\"nome\":\"Vinny\",\"idade\":31,\"id\":10201,\"risco\":0}}";

        Cliente clienteParaCadastrar = new Cliente();

        /**
         * Os dados de envio foram substituidos por um objeto e são agora serializados para serem enviados para API
         *O restAssured usa o Jackson implicitamente para essa serialização
         */
        clienteParaCadastrar.setNome("Vinny");
        clienteParaCadastrar.setIdade(31);
        clienteParaCadastrar.setId(10201);

        given()
            .contentType(ContentType.JSON)
            .body(clienteParaCadastrar)
        .when().
            post(servicoCliente+recursoCliente)
        .then()
            .statusCode(201)
            .body(equalTo(respostaEsperada));
        // Observe que dessa forma a resposta inteira está como string, e fazemos o match dela inteira
    }


    @Test
    @DisplayName("Quando eu cadastrar um cliente, Então ele deve ser salvo com sucesso - Forma 2 de ser fazer")
    public void quandoCadastrarCliente_EntaoEleDeveSerSalvoComSucessoForma2() {
        apagaTodosClientesDoServidor();

        Cliente clienteParaCadastrar = new Cliente();

        clienteParaCadastrar.setNome("Vinny");
        clienteParaCadastrar.setIdade(31);
        clienteParaCadastrar.setId(10201);

        given()
                .contentType(ContentType.JSON)
                .body(clienteParaCadastrar)
                .when().
                post(servicoCliente+recursoCliente)
                .then()
                .statusCode(201)
                .body("10201.nome", equalTo("Vinny"))
                .body("10201.idade", equalTo(31))
                .body("10201.id", equalTo(10201));
            // Observe que dessa forma estarmos verificando elemento por elemento do Json da resposta.
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
     * Incluindo como hook para rodar ao final de cada  teste e deixar o servidor no mesmo estado em que estava antes.
     * Chamado explicitamente em alguns testes também como preparação
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
