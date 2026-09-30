package dominio.cliente;

import dominio.usuario.Loja;

import java.util.Objects;

public class Cliente {
    private String nome;
    private final String cpf;
    private String telefone;
    private String endereco;
    private String cidade;

    // Cada cliente pertence a uma loja (REGRAS_NEGOCIO.md, seção 2.2.1).
    private final Loja loja;

    public Cliente(String nome, String cpf, String telefone, String endereco, String cidade, Loja loja) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("O CPF do cliente é obrigatório.");
        }
        this.cpf = cpf.trim();
        this.loja = Objects.requireNonNull(loja, "A loja do cliente é obrigatória.");
        alterarDados(nome, telefone, endereco, cidade);
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public Loja getLoja() {
        return loja;
    }

    public boolean pertenceA(Loja loja) {
        return this.loja == loja;
    }

    public void alterarDados(String nome, String telefone, String endereco, String cidade) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do cliente é obrigatório.");
        }
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.cidade = cidade;
    }
}
