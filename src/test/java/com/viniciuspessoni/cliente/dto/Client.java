package com.viniciuspessoni.cliente.dto;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

/**
 * Objeto simples (POJO) para guardar informaçōes de clientes nos testes
 */

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class Client {

    private String nome;
    private int idade;
    private int id;

    public Client(){
        nome = "N/D";
        idade = 0;
        id = 0;
    }

    public Client(String nome, int idade, int id){
        this.nome = nome;
        this.idade = idade;
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}