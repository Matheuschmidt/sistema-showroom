package dominio.usuario;

public class Perfil {
    private String login;
    private String senha;

    public Perfil(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }
}
