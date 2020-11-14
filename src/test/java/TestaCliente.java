
import io.restassured.http.ContentType;

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
    @DisplayName("Quando eu cadastrar um cliente, Então ele deve ser salvo com sucesso - Forma 1 de ser fazer. " +
            "Menos indicada nesse caso")
    public void quandoCadastrarCliente_EntaoEleDeveSerSalvoComSucesso() {

        apagaTodosClientesDoServidor();

        String respostaEsperada = "{\"10101\":{\"nome\":\"Vinny\",\"idade\":31,\"id\":10101,\"risco\":0}}";

        Cliente clienteParaCadastrar = new Cliente();

        /**
         * Os dados de envio foram substituidos por um objeto e são agora serializados para serem enviados para API
         *O restAssured usa o Jackson implicitamente para essa serialização
         */
        clienteParaCadastrar.setNome("Vinny");
        clienteParaCadastrar.setIdade(31);
        clienteParaCadastrar.setId(10101);

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
    @DisplayName("Quando eu cadastrar um cliente, Então ele deve ser salvo com sucesso - Forma 2 de ser fazer." +
            " Mais indicada nesse caso")
    public void quandoCadastrarCliente_EntaoEleDeveSerSalvoComSucessoForma2() {

        Cliente clienteParaCadastrar = new Cliente();

        clienteParaCadastrar.setNome("Vinny");
        clienteParaCadastrar.setIdade(31);
        clienteParaCadastrar.setId(20101);

        given()
                .contentType(ContentType.JSON)
                .body(clienteParaCadastrar)
        .when()
                .post(servicoCliente+recursoCliente)
        .then()
                .statusCode(201)
                .body("20101.nome", equalTo("Vinny"))
                .body("20101.idade", equalTo(31))
                .body("20101.id", equalTo(20101));
            // Observe que dessa forma estarmos verificando elemento por elemento do Json da resposta.
    }

    @Test
    @DisplayName("Quando eu atualizar um cliente, Então ele deve ser atualizado com sucesso")
    public void quandoAtualizarCliente_EntaoEleDeveSerAtualizadoComSucesso() {

        Cliente clienteParaCadastrar = new Cliente();

        clienteParaCadastrar.setNome("Mickey");
        clienteParaCadastrar.setIdade(67);
        clienteParaCadastrar.setId(40101);

        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setNome("Mickey, Mouse");
        clienteAtualizado.setIdade(85);
        clienteAtualizado.setId(40101);

        given()
            .contentType(ContentType.JSON)
            .body(clienteParaCadastrar).
        when().
            post(servicoCliente+recursoCliente);

       given()
            .contentType(ContentType.JSON)
            .body(clienteAtualizado).
        when().
            put(servicoCliente+recursoCliente).
        then().
            statusCode(200).
            assertThat()
                .body("40101.id", equalTo(40101))
                .body("40101.nome", equalTo("Mickey, Mouse"))
                .body("40101.idade", equalTo(85));
    }

    @Test
    @DisplayName("Quando eu deletar um cliente, Então ele deve ser removido com sucesso")
    public void quandoDeletarCliente_EntaoEleDeveSerDeletadoComSucesso() {
        Cliente clienteParaCadastrar = new Cliente();

        clienteParaCadastrar.setNome("Tio Patinhas");
        clienteParaCadastrar.setIdade(89);
        clienteParaCadastrar.setId(40101);

        given()
                .contentType(ContentType.JSON)
                .body(clienteParaCadastrar)
        .when().
                post(servicoCliente+recursoCliente);

        given()
                .contentType(ContentType.JSON)
        .when()
                .delete(servicoCliente + recursoCliente + "/" + clienteParaCadastrar.getId())
        .then()
                .statusCode(200)
                .assertThat().body(not(contains("Tio Patinhas")));
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
