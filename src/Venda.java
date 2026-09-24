public class Venda {
    private Cliente cliente;
    private Loja loja;
    private Alianca alianca1;
    private Alianca alianca;
    private String vendedor;
    private String dataVenda;
    private String dataEntrega;
    private double cotacaoOuro;
    private double valorBruto;
    private double desconto;
    private double valorFinal;

    public Venda(Cliente cliente, Loja loja, Alianca alianca1, Alianca alianca, String vendedor, String dataVenda, String dataEntrega, double desconto) {
        this.cliente = cliente;
        this.loja = loja;
        this.alianca1 = alianca1;
        this.alianca = alianca;
        this.vendedor = vendedor;
        this.dataVenda = dataVenda;
        this.dataEntrega = dataEntrega;
        this.desconto = desconto;
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

    public Alianca getAlianca() {
        return alianca;
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

    public double getCotacaoOuro() {
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

    public void exibirVenda(){
        System.out.println("========== VENDA ==========");
        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("Loja: " + loja.getNome());
        System.out.println("Vendedor: " + vendedor);
        System.out.println("Data da venda: " + dataVenda);
        System.out.println("Data de entrega: " + dataEntrega);
        System.out.println("\n-------------------------");
        alianca.exibirAlianca();
        System.out.println("\n-------------------------");
        alianca1.exibirAlianca();

    }
}
