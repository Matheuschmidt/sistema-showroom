package venda;

import dominio.cliente.Cliente;
import dominio.produto.Alianca;
import dominio.produto.Modelo;
import dominio.usuario.Loja;

import java.util.ArrayList;
import java.util.List;

public class PedidoVenda {
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
    public PedidoVenda(Cliente cliente, Loja loja, String vendedor, String dataVenda, String dataEntrega, CotacaoOuro cotacaoOuro) {
        this.cliente = cliente;
        this.loja = loja;
        this.vendedor = vendedor;
        this.dataVenda = dataVenda;
        this.dataEntrega = dataEntrega;
        this.cotacaoOuro = cotacaoOuro;
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

    public void adicionarAlianca(Modelo modelo, String tipoAro, double aro, String gravacao, int teorOuro){
        Alianca alianca = new Alianca(modelo, tipoAro, aro, gravacao, teorOuro);
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

    public void exibirVenda(){
        System.out.println("========== VENDA ==========");
        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("Loja: " + loja.getNome());
        System.out.println("Vendedor: " + vendedor);
        System.out.println("Data da venda: " + dataVenda);
        System.out.println("Data de entrega: " + dataEntrega);
        System.out.println("\n-------------------------");
        alianca1.exibirAlianca();
        System.out.println("\n-------------------------");
        if (alianca2 != null) {
            alianca2.exibirAlianca();
        }
        System.out.printf("Peso Total: %.2f%n", calcularPesoTotal());
    }

}
