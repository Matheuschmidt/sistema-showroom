package venda;

public class Acrescimo {
    private double percentual;
    private double valorFixo;

    public Acrescimo(double percentual, double valorFixo) {
        this.percentual = percentual;
        this.valorFixo = valorFixo;
    }

    public double getPercentual() {
        return percentual;
    }

    public double getValorFixo() {
        return valorFixo;
    }
}