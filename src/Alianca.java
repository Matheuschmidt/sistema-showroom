public class Alianca {
    private Modelo modelo;
    private String tipoAro;
    private double aro;
    private String gravacao;
    private double peso;
    private int teorOuro;

    public Alianca(Modelo modelo, String tipoAro, double aro, String gravacao, double peso, int teorOuro) {
        this.modelo = modelo;
        this.tipoAro = tipoAro;
        this.aro = aro;
        this.gravacao = gravacao;
        this.peso = peso;
        this.teorOuro = teorOuro;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public String getTipoAro() {
        return tipoAro;
    }

    public double getAro() {
        return aro;
    }

    public String getGravacao() {
        return gravacao;
    }

    public double getPeso() {
        return peso;
    }

    public int getTeorOuro() {
        return teorOuro;
    }

    public void exibirAlianca(){
        System.out.println(modelo.getReferencia() + " " + getTeorOuro() + "K");
        System.out.println("ARO " + getTipoAro() + ": " + getAro() + " - " +  getGravacao());
        System.out.println("Peso: " + getPeso());
    }
}
