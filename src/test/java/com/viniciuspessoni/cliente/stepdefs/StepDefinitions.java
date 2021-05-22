package com.viniciuspessoni.cliente.stepdefs;

import com.viniciuspessoni.cliente.dto.Client;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class StepDefinitions {

    private static final String SERVICE_URL = "http://localhost:8080";
    private static final String RESOURCE_CLIENT = "/cliente";

    Client clienteToRegister;
    ValidatableResponse response;

    @Given("I have a client to register {string} with age {string} and id {string}")
    public void i_have_a_client_to_register_with_age_and_risk(String name, String age, String id) {
       clienteToRegister = new Client(name, Integer.parseInt(age), Integer.parseInt(id));
    }

    @When("I {string} this client to the service")
    public void i_this_client_to_the_service(String httMethod) {
        response = postaCliente(clienteToRegister);
    }

    @Then("I can see the client in the results list")
    public void i_can_see_the_client_in_the_results_list() {
        response
                .body("10101.nome", equalTo(clienteToRegister.getNome()))
                .body("10101.idade", equalTo(clienteToRegister.getIdade()))
                .body("10101.id", equalTo(clienteToRegister.getId()));
    }
    @And("the response HTTP code should be {string}")
    public void the_response_http_code_should_be(String string) {
       response.statusCode(HttpStatus.SC_CREATED);
    }

    /**
     * Post client to the api under test
     * @param clientToPost
     */
    private ValidatableResponse postaCliente (Client clientToPost)  {
        return given()
                .contentType(ContentType.JSON)
                .body(clientToPost)
                .when().
                        post(SERVICE_URL + RESOURCE_CLIENT)
                .then();
    }
}
