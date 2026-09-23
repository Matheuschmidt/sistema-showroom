public class Loja extends Perfil{
    private String nome;

    public Loja(String nome, String login, String senha) {
        super(login, senha);
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
