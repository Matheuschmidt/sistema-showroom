package venda;

import calculo.CalculadoraPesoProducao;
import dominio.cliente.Cliente;
import dominio.produto.Alianca;
import dominio.usuario.Loja;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PedidoVenda {
    private static final int MAXIMO_UNIDADES = 2;

    private final int id;
    private final Loja loja;
    private Cliente cliente;
    private final List<Alianca> unidades = new ArrayList<>();
    private String vendedor;
    private String dataVenda;
    private String dataEntrega;

    // Cotação congelada no momento da venda (REGRAS_NEGOCIO.md, seção 7.2).
    // CotacaoOuro é imutável, então uma nova cotação do ADMIN não altera este pedido.
    private final CotacaoOuro cotacaoOuro;

    // Alteração geral de peso da venda. Afeta somente o peso de PRODUÇÃO.
    // Não é o mesmo que "mais anatômica".
    private double percentualAlteracaoPeso;

    private final List<Acrescimo> acrescimos = new ArrayList<>();
    private double desconto;

    private StatusPedido status = StatusPedido.PEDIDO_CRIADO;
    private boolean lancado;
    private boolean editado;
    private Double pesoFinalProduzido;

    public PedidoVenda(int id, Loja loja, Cliente cliente, String vendedor, String dataVenda,
                       String dataEntrega, CotacaoOuro cotacaoOuro) {
        this.id = id;
        this.loja = Objects.requireNonNull(loja, "A loja é obrigatória.");
        this.cotacaoOuro = Objects.requireNonNull(cotacaoOuro, "A cotação do ouro é obrigatória.");
        this.cliente = validarCliente(cliente);
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

    public List<Alianca> getUnidades() {
        return List.copyOf(unidades);
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

    public double getPercentualAlteracaoPeso() {
        return percentualAlteracaoPeso;
    }

    public List<Acrescimo> getAcrescimos() {
        return List.copyOf(acrescimos);
    }

    public double getDesconto() {
        return desconto;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public boolean isEditado() {
        return editado;
    }

    public Double getPesoFinalProduzido() {
        return pesoFinalProduzido;
    }

    public boolean pertenceA(Loja loja) {
        return this.loja == loja;
    }

    // ==================== MONTAGEM E EDIÇÃO ====================

    public void definirCliente(Cliente cliente) {
        verificarEditavel();
        this.cliente = validarCliente(cliente);
        registrarEdicao();
    }

    public void adicionarAlianca(Alianca alianca) {
        verificarEditavel();
        Objects.requireNonNull(alianca, "A aliança é obrigatória.");
        if (unidades.size() >= MAXIMO_UNIDADES) {
            throw new IllegalArgumentException("Já tem um par de alianças no pedido de venda.");
        }
        unidades.add(alianca);
        registrarEdicao();
    }

    public void editarUnidade(int numeroUnidade, Alianca novaAlianca) {
        verificarEditavel();
        Objects.requireNonNull(novaAlianca, "A aliança é obrigatória.");
        if (numeroUnidade < 1 || numeroUnidade > unidades.size()) {
            throw new IllegalArgumentException("Unidade inexistente no pedido: " + numeroUnidade);
        }
        unidades.set(numeroUnidade - 1, novaAlianca);
        registrarEdicao();
    }

    public void editarDadosVenda(String vendedor, String dataVenda, String dataEntrega) {
        verificarEditavel();
        boolean mudou = !Objects.equals(this.vendedor, vendedor)
                || !Objects.equals(this.dataVenda, dataVenda)
                || !Objects.equals(this.dataEntrega, dataEntrega);
        this.vendedor = vendedor;
        this.dataVenda = dataVenda;
        this.dataEntrega = dataEntrega;
        if (mudou) {
            registrarEdicao();
        }
    }

    public void definirPercentualAlteracaoPeso(double percentual) {
        verificarEditavel();
        if (percentual <= -100) {
            throw new IllegalArgumentException("A alteração de peso deve ser maior que -100%.");
        }
        if (percentual != this.percentualAlteracaoPeso) {
            this.percentualAlteracaoPeso = percentual;
            registrarEdicao();
        }
    }

    public void adicionarAcrescimo(Acrescimo acrescimo){
        verificarEditavel();
        acrescimos.add(Objects.requireNonNull(acrescimo, "O acréscimo é obrigatório."));
        registrarEdicao();
    }

    public void removerAcrescimos() {
        verificarEditavel();
        if (!acrescimos.isEmpty()) {
            acrescimos.clear();
            registrarEdicao();
        }
    }

    public void aplicarDesconto(double percentual){
        verificarEditavel();
        if (percentual < 0 || percentual > 100) {
            throw new IllegalArgumentException("O desconto deve estar entre 0% e 100%.");
        }
        if (percentual != this.desconto) {
            this.desconto = percentual;
            registrarEdicao();
        }
    }

    // Chamado pelo sistema quando o pedido é registrado. A partir daqui,
    // qualquer alteração marca o pedido como *Editado* para a fábrica.
    public void confirmarLancamento() {
        if (lancado) {
            throw new IllegalStateException("O pedido " + id + " já foi lançado.");
        }
        if (unidades.isEmpty()) {
            throw new IllegalStateException("O pedido precisa ter pelo menos uma unidade.");
        }
        lancado = true;
    }

    // Editável enquanto não estiver FINALIZADO (nem ENTREGUE ou CANCELADO).
    public boolean podeSerEditado() {
        return status == StatusPedido.PEDIDO_CRIADO || status == StatusPedido.OK;
    }

    // ==================== PESO COMERCIAL ====================

    public double calcularPesoComercial() {
        double pesoComercial = 0;
        for (Alianca unidade : unidades) {
            pesoComercial += unidade.calcularPesoComercial();
        }
        return pesoComercial;
    }

    // ==================== PESO DE PRODUÇÃO ====================

    public double calcularPesoProducao(Alianca unidade) {
        return CalculadoraPesoProducao.calcularPesoUnidade(unidade, percentualAlteracaoPeso);
    }

    // Cada unidade já vem arredondada individualmente; aqui só somamos.
    public double calcularPesoProducaoTotal() {
        double pesoTotal = 0;
        for (Alianca unidade : unidades) {
            pesoTotal += calcularPesoProducao(unidade);
        }
        return pesoTotal;
    }

    // ==================== VALOR DA VENDA ====================
    // Ordem (REGRAS_NEGOCIO.md, seção 7.3):
    // valor base → acréscimo % → acréscimo em R$ → desconto % → valor final

    // Soma de (peso comercial da unidade × cotação congelada do teor da unidade).
    public double calcularValorBase() {
        double valorBase = 0;
        for (Alianca unidade : unidades) {
            valorBase += unidade.calcularValorBase(cotacaoOuro);
        }
        return valorBase;
    }

    public double calcularAcrescimoPercentual() {
        double valorBase = calcularValorBase();
        double total = 0;
        for (Acrescimo acrescimo : acrescimos) {
            total += valorBase * (acrescimo.getPercentual() / 100);
        }
        return total;
    }

    public double calcularAcrescimoFixo() {
        double total = 0;
        for (Acrescimo acrescimo : acrescimos) {
            total += acrescimo.getValorFixo();
        }
        return total;
    }

    public double calcularAcrescimos(){
        return calcularAcrescimoPercentual() + calcularAcrescimoFixo();
    }

    public double calcularValorFinal(){
        double subtotal = calcularValorBase() + calcularAcrescimos();
        double valorDesconto = subtotal * (desconto / 100);
        return subtotal - valorDesconto;
    }

    // ==================== STATUS ====================

    // Imprimir na fábrica muda automaticamente PEDIDO CRIADO → OK.
    // Reimpressões (ou pedidos cancelados) não mudam o status.
    public void registrarImpressaoFabrica() {
        if (status == StatusPedido.PEDIDO_CRIADO) {
            status = StatusPedido.OK;
        }
    }

    public boolean podeSerFinalizado() {
        return status == StatusPedido.OK;
    }

    public void finalizar(Double pesoFinalProduzido) {
        if (!podeSerFinalizado()) {
            throw new IllegalStateException("Só é possível finalizar um pedido com status OK "
                    + "(impresso pela fábrica). Status atual: " + status + ".");
        }
        if (pesoFinalProduzido != null && pesoFinalProduzido <= 0) {
            throw new IllegalArgumentException("O peso final produzido deve ser maior que zero.");
        }
        this.pesoFinalProduzido = pesoFinalProduzido;
        status = StatusPedido.FINALIZADO;
    }

    public void marcarEntregue() {
        if (status != StatusPedido.FINALIZADO) {
            throw new IllegalStateException("Só é possível marcar como ENTREGUE um pedido FINALIZADO. "
                    + "Status atual: " + status + ".");
        }
        status = StatusPedido.ENTREGUE;
    }

    // Cancelamento não apaga o pedido: ele continua no sistema com status CANCELADO.
    public void cancelar() {
        if (status == StatusPedido.CANCELADO) {
            throw new IllegalStateException("O pedido " + id + " já está cancelado.");
        }
        status = StatusPedido.CANCELADO;
    }

    public boolean contabilizaNoFaturamento() {
        return status != StatusPedido.CANCELADO;
    }

    // ==================== AUXILIARES ====================

    private Cliente validarCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "O cliente é obrigatório.");
        if (!cliente.pertenceA(loja)) {
            throw new IllegalArgumentException("O cliente não pertence a esta loja.");
        }
        return cliente;
    }

    private void verificarEditavel() {
        if (!podeSerEditado()) {
            throw new IllegalStateException("O pedido " + id + " está " + status
                    + " e não pode mais ser editado.");
        }
    }

    private void registrarEdicao() {
        if (lancado) {
            editado = true;
        }
    }
}
