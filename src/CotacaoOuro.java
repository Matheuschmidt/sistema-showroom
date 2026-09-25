public class CotacaoOuro {
    private double cotacao10k;
    private double cotacao18k;

    public CotacaoOuro(double cotacao10k, double cotacao18k) {
        this.cotacao10k = cotacao10k;
        this.cotacao18k = cotacao18k;
    }

    public double getCotacao10k() {
        return cotacao10k;
    }

    public double getCotacao18k() {
        return cotacao18k;
    }

    public void setCotacao10k(double cotacao10k) {
        this.cotacao10k = cotacao10k;
    }

    public void setCotacao18k(double cotacao18k) {
        this.cotacao18k = cotacao18k;
    }
}
