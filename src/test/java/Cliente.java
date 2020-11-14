/**
 * Objeto simples (POJO) para guardar informaçōes de clientes nos testes
 */
public class Cliente {

    private String nome;
    private int idade;
    private int id;

    public Cliente(){
        nome = "N/D";
        idade = 0;
        id = 0;
    }

    public Cliente(String nome, int idade, int id){
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