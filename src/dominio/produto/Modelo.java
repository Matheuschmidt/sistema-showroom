package dominio.produto;

import venda.CotacaoOuro;

public class Modelo {
    private String referencia;
    private double largura;
    private double aroBase;
    private double pesoBase10k;
    private double pesoBase18k;
    private double valorBase10k;
    private double valorBase18k;

    public Modelo(String referencia, double largura, double aroBase, double pesoBase10k, double pesoBase18k) {
        this.referencia = referencia;
        this.largura = largura;
        this.aroBase = aroBase;
        this.pesoBase10k = pesoBase10k;
        this.pesoBase18k = pesoBase18k;
    }

    public String getReferencia() {
        return referencia;
    }

    public double getLargura() {
        return largura;
    }

    public double getAroBase() {
        return aroBase;
    }

    public double getPesoBase10k() {
        return pesoBase10k;
    }

    public double getPesoBase18k() {
        return pesoBase18k;
    }

    public double getValorBase10k() {
        return valorBase10k;
    }

    public double getValorBase18k() {
        return valorBase18k;
    }

    // Peso base de UMA unidade no aro 20, conforme o teor (REGRAS_NEGOCIO.md, seção 3).
    public double getPesoBase(TeorOuro teorOuro) {
        return switch (teorOuro) {
            case K10 -> pesoBase10k;
            case K18 -> pesoBase18k;
        };
    }

    // Valor base de uma unidade: peso comercial × cotação do seu teor.
    public double calcularValorBase(TeorOuro teorOuro, CotacaoOuro cotacaoOuro){
        return getPesoBase(teorOuro) * cotacaoOuro.getCotacao(teorOuro);
    }
}
