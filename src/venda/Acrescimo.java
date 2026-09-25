package venda;

public class Acrescimo {
    private String descricao;
    private double percentual;
    private double valorFixo;

    public Acrescimo(String descricao, double percentual, double valorFixo) {
        this.descricao = descricao;
        this.percentual = percentual;
        this.valorFixo = valorFixo;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPercentual() {
        return percentual;
    }

    public double getValorFixo() {
        return valorFixo;
    }
}