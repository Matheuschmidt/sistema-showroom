public class Modelo {
    private String referencia;
    private double largura;
    private double aroBase;
    private double pesoBase10k;
    private double pesoBase18k;

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
}
