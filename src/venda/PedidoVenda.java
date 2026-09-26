package venda;

import dominio.cliente.Cliente;
import dominio.produto.Alianca;
import dominio.produto.Modelo;
import dominio.usuario.Loja;

import java.util.ArrayList;
import java.util.List;

public class PedidoVenda {
    private int id;
    private Cliente cliente;
    private Loja loja;
    private Alianca alianca1;
    private Alianca alianca2;
    private String vendedor;
    private String dataVenda;
    private String dataEntrega;
    private CotacaoOuro cotacaoOuro;
    private double valorBruto;
    private double desconto;
    private double valorFinal;
    private String pe;

    private List<Acrescimo> acrescimos = new ArrayList<>();
    public PedidoVenda(int id, Loja loja, String vendedor, String dataVenda, String dataEntrega) {
        this.id = id;
        this.loja = loja;
        this.vendedor = vendedor;
        this.dataVenda = dataVenda;
        this.dataEntrega = dataEntrega;
    }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Loja getLoja() {
        return loja;
    }

    public Alianca getAlianca1() {
        return alianca1;
    }

    public Alianca getAlianca2() {
        return alianca2;
    }

    public String getVendedor() {
        return vendedor;
    }

    public String getDataVenda() {
        return dataVenda;
    }

    public String getDataEntrega() {
        return dataEntrega;
    }

    public CotacaoOuro getCotacaoOuro() {
        return cotacaoOuro;
    }

    public double getValorBruto() {
        return valorBruto;
    }

    public double getDesconto() {
        return desconto;
    }

    public double getValorFinal() {
        return valorFinal;
    }

    public String getPe() {
        return pe;
    }

    public void definirPe(String pe){
        this.pe = pe;
    }

    public void definirCliente(Cliente cliente){
        this.cliente = cliente;
    }

    public void definirCotacaoOuro(CotacaoOuro cotacaoOuro){
        this.cotacaoOuro = cotacaoOuro;
    }
    public void adicionarAlianca(Modelo modelo, String tipoAro, double aro, String gravacao, int teorOuro, double percentualAlteracaoPeso){
        Alianca alianca = new Alianca(modelo, tipoAro, aro, gravacao, teorOuro, percentualAlteracaoPeso);
        if (alianca1 == null){
            alianca1 = alianca;
        }else if (alianca2 == null){
            alianca2 = alianca;
        }else {
            throw new IllegalArgumentException("Já tem um par de alianças no pedido de venda.");
        }
    }

    public void adicionarAcrescimo(Acrescimo acrescimo){
        acrescimos.add(acrescimo);
    }

    public double calcularPesoTotal(){
        double pesoTotal = 0;
        if (alianca1 != null){
            pesoTotal += alianca1.calcularPeso();
        }
        if (alianca2 != null){
            pesoTotal += alianca2.calcularPeso();
        }
        return pesoTotal;
    }

    public void calcularValorBruto(){
        valorBruto = 0;

        if (alianca1 != null){
            valorBruto += alianca1.getModelo().calcularValorBase(alianca1.getTeorOuro(), cotacaoOuro);
        }
        if (alianca2 != null){
            valorBruto += alianca2.getModelo().calcularValorBase(alianca2.getTeorOuro(), cotacaoOuro);
        }
    }

    public double calcularAcrescimos(){
        double totalAcrescimos = 0;
        for (Acrescimo acrescimo : acrescimos){
            totalAcrescimos += valorBruto * (acrescimo.getPercentual() / 100);
            totalAcrescimos += acrescimo.getValorFixo();
        }
        return totalAcrescimos;
    }

    public void aplicarDesconto(double percentual){
        desconto = percentual;
    }

    public void calcularValorFinal(){
        double subtotal = valorBruto + calcularAcrescimos();
        double valorDesconto = subtotal * (desconto / 100);

        valorFinal = subtotal - valorDesconto;
    }

    public void exibirVenda() {
        System.out.println("========== VENDA ==========");
        System.out.println("Pedido Nº: " + id);
        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("CPF: " + cliente.getCpf());
        System.out.println("Loja: " + loja.getNome());
        System.out.println("Vendedor: " + vendedor);
        System.out.println("Data da venda: " + dataVenda);
        System.out.println("Data de entrega: " + dataEntrega);

        System.out.println("\n===========================");

        // Apenas uma unidade
        if (alianca2 == null) {

            System.out.println("Uma unidade de aliança "
                    + alianca1.getModelo().getReferencia()
                    + " em " + alianca1.getTeorOuro() + "K");

            if (pe != null && !pe.isBlank()) {
                System.out.println("PE: " + pe);
            }

            System.out.println("\nAro " + alianca1.getTipoAro()
                    + ": " + alianca1.getAro());

            System.out.println("\nGravação: " + alianca1.getGravacao());

            double valorAlianca = alianca1.getModelo()
                    .calcularValorBase(alianca1.getTeorOuro(), cotacaoOuro);

            System.out.printf("\nValor: R$ %.2f%n", valorAlianca);

        } else {

            boolean mesmoModelo = alianca1.getModelo().getReferencia()
                    .equals(alianca2.getModelo().getReferencia());

            boolean mesmoTeor = alianca1.getTeorOuro()
                    == alianca2.getTeorOuro();

            // Par do mesmo modelo e mesmo teor
            if (mesmoModelo && mesmoTeor) {

                double valorPar = alianca1.getModelo()
                        .calcularValorBase(
                                alianca1.getTeorOuro(),
                                cotacaoOuro
                        ) * 2;

                System.out.println("Par de alianças "
                        + alianca1.getModelo().getReferencia()
                        + " em " + alianca1.getTeorOuro() + "K");

                if (pe != null && !pe.isBlank()) {
                    System.out.println("PE: " + pe);
                }

                System.out.println("\nAro " + alianca1.getTipoAro()
                        + ": " + alianca1.getAro());

                System.out.println("\nGravação: "
                        + alianca1.getGravacao());

                System.out.println("\nAro " + alianca2.getTipoAro()
                        + ": " + alianca2.getAro());

                System.out.println("\nGravação: "
                        + alianca2.getGravacao());

                System.out.printf("\nValor do par: R$ %.2f%n", valorPar);

            } else {

                // Duas unidades de modelos ou teores diferentes

                double valorAlianca1 = alianca1.getModelo()
                        .calcularValorBase(
                                alianca1.getTeorOuro(),
                                cotacaoOuro
                        );

                double valorAlianca2 = alianca2.getModelo()
                        .calcularValorBase(
                                alianca2.getTeorOuro(),
                                cotacaoOuro
                        );

                System.out.println("Uma unidade de aliança "
                        + alianca1.getModelo().getReferencia()
                        + " em " + alianca1.getTeorOuro() + "K");

                if (pe != null && !pe.isBlank()) {
                    System.out.println("PE: " + pe);
                }

                System.out.println("\nAro " + alianca1.getTipoAro()
                        + ": " + alianca1.getAro());

                System.out.println("\nGravação: "
                        + alianca1.getGravacao());

                System.out.printf("\nValor: R$ %.2f%n", valorAlianca1);

                System.out.println("\n---------------------------");

                System.out.println("Uma unidade de aliança "
                        + alianca2.getModelo().getReferencia()
                        + " em " + alianca2.getTeorOuro() + "K");

                if (pe != null && !pe.isBlank()) {
                    System.out.println("PE: " + pe);
                }

                System.out.println("\nAro " + alianca2.getTipoAro()
                        + ": " + alianca2.getAro());

                System.out.println("\nGravação: "
                        + alianca2.getGravacao());

                System.out.printf("\nValor: R$ %.2f%n", valorAlianca2);
            }
        }

        System.out.println("\n===========================");
        System.out.printf("Valor bruto: R$ %.2f%n", valorBruto);
        System.out.printf("Acréscimos: R$ %.2f%n", calcularAcrescimos());
        System.out.printf("Desconto: %.2f%%%n", desconto);
        System.out.printf("VALOR A PAGAR: R$ %.2f%n", valorFinal);
        System.out.println("===========================");
    }

}
