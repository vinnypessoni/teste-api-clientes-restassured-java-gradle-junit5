
import io.restassured.http.ContentType;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;

import org.hamcrest.core.IsEqual;


public class TestaCliente {

    private String servicoCliente = "http://localhost:8080";
    private String recursoCliente = "/cliente";
    private String apagaTodosClientes = "/apagaTodos";
    private static final String listaClientesVazia = "{}";

    @Test
    @DisplayName("Quando eu requisitar a lista de clientes sem adicionar clientes antes, Então ela deve estar vazia")
    public void quandoRequisitarListaClientesSemAdicionar_EntaoElaDeveEstarVazia() {

        /**
         * Observe que aqui chamamos o método de apoio para apagar todos os clientes do servidor como forma de preparar
         * o servidor para esse teste que tem como pré condição ele estar vazio.
         */
        apagaTodosClientesDoServidor();

        given()
            .contentType(ContentType.JSON)
        .when()
            .get(servicoCliente)
        .then()
            .statusCode(200)
            .body(equalTo(listaClientesVazia));
    }

    @Test
    @DisplayName("Quando eu cadastrar um cliente, Então ele deve ser salvo com sucesso")
    public void quandoCadastrarCliente_EntaoEleDeveSerSalvoComSucesso() {

        Cliente clienteParaCadastrar = new Cliente("Vinny", 31,10101 );

        postaCliente(clienteParaCadastrar)
                .statusCode(201)
                .body("10101.nome", equalTo("Vinny"))
                .body("10101.idade", equalTo(31))
                .body("10101.id", equalTo(10101));
    }

    @Test
    @DisplayName("Quando eu atualizar um cliente, Então ele deve ser atualizado com sucesso")
    public void quandoAtualizarCliente_EntaoEleDeveSerAtualizadoComSucesso() {

        Cliente clienteParaCadastrar = new Cliente("Mickey", 67, 40101);

        postaCliente(clienteParaCadastrar);

        clienteParaCadastrar.setNome("Mickey, Mouse");
        clienteParaCadastrar.setIdade(85);
        clienteParaCadastrar.setId(40101);

        atualizaCliente(clienteParaCadastrar)
            .statusCode(200)
            .body("40101.id", equalTo(40101))
            .body("40101.nome", equalTo("Mickey, Mouse"))
            .body("40101.idade", equalTo(85));
    }

    @Test
    @DisplayName("Quando eu deletar um cliente, Então ele deve ser removido com sucesso")
    public void quandoDeletarCliente_EntaoEleDeveSerDeletadoComSucesso() {

        Cliente cliente = new Cliente("Tio Patinhas", 89, 40101);

        postaCliente(cliente);

        apagaCliente(cliente)
                .statusCode(200)
                .assertThat().body(not(contains("Tio Patinhas")));
    }

    /**
     * Posta cliente para nossa API de teste
     * @param clienteParaPostar
     */
    private ValidatableResponse postaCliente (Cliente clienteParaPostar)  {
       return given()
                .contentType(ContentType.JSON)
                .body(clienteParaPostar)
                .when().
                post(servicoCliente+recursoCliente)
                .then();
    }

    /**
     * Atualiza cliente na nossa API de teste
     * @param clienteParaAtualizar
     * @return
     */
    private ValidatableResponse atualizaCliente (Cliente clienteParaAtualizar) {
       return given()
                .contentType(ContentType.JSON)
                .body(clienteParaAtualizar).
                when().
                put(servicoCliente+recursoCliente).
                then();
    }

    private ValidatableResponse apagaCliente (Cliente clienteApagar) {
       return  given()
               .contentType(ContentType.JSON)
               .when()
               .delete(servicoCliente + recursoCliente + "/" + clienteApagar.getId())
               .then();
    }

    /**
     * Método de apoio para apagar todos os clientes do servidor.
     * Usado para teste apenas.
     * Incluindo como hook para rodar ao final de cada  teste e deixar o servidor no mesmo estado em que estava antes.
     * Chamado explicitamente em alguns testes também como preparação
     */
    private void apagaTodosClientesDoServidor(){
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
