
import io.restassured.http.ContentType;

import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;

import org.hamcrest.core.IsEqual;


public class TestaCliente {

    private static final String SERVICO_CLIENTE = "http://localhost:8080";
    private static final String RECURSO_CLIENTE = "/cliente";
    private static final String APAGA_TODOS_CLIENTES = "/apagaTodos";
    private static final String RISCO = "/risco/";
    private static final String LISTA_CLIENTES_VAZIA = "{}";

    @Test
    @DisplayName("Quando eu requisitar a lista de clientes sem adicionar clientes antes, Então ela deve estar vazia")
    public void quandoRequisitarListaClientesSemAdicionar_EntaoElaDeveEstarVazia() {

        apagaTodosClientesDoServidor();

        pegaTodosClientes()
            .statusCode(HttpStatus.SC_OK)
            .body(equalTo(LISTA_CLIENTES_VAZIA));
    }

    @Test
    @DisplayName("Quando eu cadastrar um cliente, Então ele deve ser salvo com sucesso")
    public void quandoCadastrarCliente_EntaoEleDeveSerSalvoComSucesso() {

        Cliente clienteParaCadastrar = new Cliente("Vinny", 31,10101 );

        postaCliente(clienteParaCadastrar)
                .statusCode(HttpStatus.SC_CREATED)
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
            .statusCode(HttpStatus.SC_OK)
            .body("40101.id", equalTo(40101))
            .body("40101.nome", equalTo("Mickey, Mouse"))
            .body("40101.idade", equalTo(85));
    }

    @Test
    @DisplayName("Quando eu deletar um cliente, Então ele deve ser removido com sucesso")
    public void quandoDeletarCliente_EntaoEleDeveSerDeletadoComSucesso() {

        // Arrange
        Cliente cliente = new Cliente("Tio Patinhas", 89, 40101);

        // Act
        postaCliente(cliente);

        // Act/Assert
        apagaCliente(cliente)
                .statusCode(HttpStatus.SC_OK)
                .assertThat().body(not(contains("Tio Patinhas")));
    }

    @Test
    @DisplayName("Quando eu solicitar o risco de um cliente com credenciais válidas, Então ele deve ser retornado com sucesso")
    public void quandoSolicitarRiscoComAutorização () {

        Cliente cliente = new Cliente("Mickey Mouse", 32, 220389);

        int riscoEsperado = - 50;

        postaCliente(cliente);

         given()
                .auth()
                .basic("aluno", "senha")
        .when()
                .get(SERVICO_CLIENTE+RISCO+cliente.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK).body("risco", equalTo(riscoEsperado));
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
                post(SERVICO_CLIENTE + RECURSO_CLIENTE)
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
                put(SERVICO_CLIENTE + RECURSO_CLIENTE).
                then();
    }

    /**
     * Apaga um cliente em específico da nossa API de teste
     * @param clienteApagar
     * @return
     */
    private ValidatableResponse apagaCliente (Cliente clienteApagar) {
       return  given()
               .contentType(ContentType.JSON)
               .when()
               .delete(SERVICO_CLIENTE + RECURSO_CLIENTE + "/" + clienteApagar.getId())
               .then();
    }

    /**
     * Pega todos os clientes cadastrados na API
     * @return lista com todos os clientes wrapped no tipo de resposta do restAssured
     */
    private ValidatableResponse pegaTodosClientes () {
       return  given()
                .contentType(ContentType.JSON)
                .when()
                .get(SERVICO_CLIENTE)
                .then();
    }

    /**
     * Método de apoio para apagar todos os clientes do servidor.
     * Usado para teste apenas.
     * Incluindo como hook para rodar ao final de cada  teste e deixar o servidor no mesmo estado em que estava antes.
     * Chamado explicitamente em alguns testes também como preparação
     */
    @AfterEach
    private void apagaTodosClientesDoServidor(){

        when()
            .delete(SERVICO_CLIENTE + RECURSO_CLIENTE + APAGA_TODOS_CLIENTES)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .assertThat().body(new IsEqual(LISTA_CLIENTES_VAZIA));
    }
}
