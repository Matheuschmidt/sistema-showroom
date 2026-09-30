package dominio.produto;

import venda.CotacaoOuro;

import java.util.Objects;

// Uma unidade (aliança) da venda. Cada unidade tem suas próprias características.
// A classe é imutável: para editar uma unidade, o pedido troca a unidade inteira.
public class Alianca {
    private final Modelo modelo;
    private final TipoAro tipoAro;
    private final double aro;
    private final String gravacao;
    private final TeorOuro teorOuro;
    private final Double larguraPE;
    private final boolean maisAnatomica;

    public Alianca(Modelo modelo, TipoAro tipoAro, double aro, String gravacao, TeorOuro teorOuro,
                   Double larguraPE, boolean maisAnatomica) {
        this.modelo = Objects.requireNonNull(modelo, "O modelo é obrigatório.");
        this.tipoAro = Objects.requireNonNull(tipoAro, "O tipo de aro é obrigatório.");
        this.teorOuro = Objects.requireNonNull(teorOuro, "O teor do ouro é obrigatório.");

        if (aro <= 0) {
            throw new IllegalArgumentException("O aro deve ser maior que zero.");
        }
        if (larguraPE != null && larguraPE <= 0) {
            throw new IllegalArgumentException("A largura do P.E. deve ser maior que zero.");
        }

        this.aro = aro;
        this.gravacao = gravacao == null ? "" : gravacao;
        this.larguraPE = larguraPE;
        this.maisAnatomica = maisAnatomica;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public TipoAro getTipoAro() {
        return tipoAro;
    }

    public double getAro() {
        return aro;
    }

    public String getGravacao() {
        return gravacao;
    }

    public TeorOuro getTeorOuro() {
        return teorOuro;
    }

    // Largura desejada do P.E., ou null quando não há P.E.
    public Double getLarguraPE() {
        return larguraPE;
    }

    public boolean possuiPE() {
        return larguraPE != null;
    }

    public boolean isMaisAnatomica() {
        return maisAnatomica;
    }

    // Peso comercial: peso base do modelo para o teor da unidade (REGRAS_NEGOCIO.md, seção 4).
    // Não sofre influência de aro, P.E. ou alterações de produção.
    public double calcularPesoComercial() {
        return modelo.getPesoBase(teorOuro);
    }

    public double calcularValorBase(CotacaoOuro cotacaoOuro) {
        return modelo.calcularValorBase(teorOuro, cotacaoOuro);
    }
}
