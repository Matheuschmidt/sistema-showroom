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

    public double calcularValorBase(int teorOuro, CotacaoOuro cotacaoOuro){
        if (teorOuro == 10){
            return pesoBase10k * cotacaoOuro.getCotacao10k();
        }
        if (teorOuro == 18){
            return pesoBase18k * cotacaoOuro.getCotacao18k();
        }
        throw new IllegalArgumentException("Teor de ouro inválido.");
    }
}
