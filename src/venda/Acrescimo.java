package venda;

public class Acrescimo {
    private double percentual;
    private double valorFixo;

    public Acrescimo(double percentual, double valorFixo) {
        if (percentual < 0 || valorFixo < 0) {
            throw new IllegalArgumentException("Acréscimos não podem ser negativos.");
        }
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