public class Alianca {
    private Modelo modelo;
    private String tipoAro;
    private double aro;
    private String gravacao;
    private int teorOuro;

    private static final IndiceAro INDICE_ARO = new IndiceAro();

    public Alianca(Modelo modelo, String tipoAro, double aro, String gravacao, int teorOuro) {
        this.modelo = modelo;
        this.tipoAro = tipoAro;
        this.aro = aro;
        this.gravacao = gravacao;
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

    public int getTeorOuro() {
        return teorOuro;
    }

    public double calcularPeso(){
        double pesoBase;
        int aroInteiro = (int) Math.ceil(aro);
        double peso;
        double pesoArredondado;
        if (teorOuro == 10){
            pesoBase = modelo.getPesoBase10k();
        } else if (teorOuro == 18){
            pesoBase = modelo.getPesoBase18k();
        }else {
            throw new IllegalArgumentException("Teor de ouro inválido.");
        }
        peso = pesoBase * INDICE_ARO.buscarIndice(aroInteiro);
        pesoArredondado = Math.round(peso / 0.05) * 0.05;
        return pesoArredondado;
    }

    public void exibirAlianca(){
        System.out.println(modelo.getReferencia() + " " + getTeorOuro() + "K");
        System.out.println("ARO " + getTipoAro() + ": " + getAro() + " - " +  getGravacao());
        System.out.printf("Peso: %.2f%n", calcularPeso());
    }
}
