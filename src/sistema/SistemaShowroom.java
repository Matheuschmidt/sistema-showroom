package sistema;

import catalogo.CatalogoModelos;
import dominio.cliente.Cliente;
import dominio.usuario.Loja;
import dominio.usuario.Perfil;
import venda.CotacaoOuro;
import venda.PedidoVenda;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SistemaShowroom {
    private CatalogoModelos catalogoModelos = new CatalogoModelos();

    public CatalogoModelos getCatalogoModelos() {
        return catalogoModelos;
    }

    private List<PedidoVenda> pedidos = new ArrayList<>();
    private int proximoIdPedido = 1;

    private List<Cliente> clientes = new ArrayList<>();

    private List<Perfil> perfis = new ArrayList<>();
    private List<Loja> lojas = new ArrayList<>();

    // Cotação vigente para NOVAS vendas. Começa vazia: o ADMIN precisa cadastrá-la.
    private CotacaoOuro cotacaoAtual;

    // ==================== PERFIS ====================

    public void adicionarPerfil(Perfil perfil) {
        perfis.add(perfil);
        if (perfil instanceof Loja loja) {
            lojas.add(loja);
        }
    }

    public Optional<Perfil> autenticar(String login, String senha) {
        for (Perfil perfil : perfis) {
            if (perfil.getLogin().equals(login) && perfil.getSenha().equals(senha)) {
                return Optional.of(perfil);
            }
        }
        return Optional.empty();
    }

    public List<Loja> getLojas() {
        return List.copyOf(lojas);
    }

    // ==================== COTAÇÃO ====================

    // Cria um NOVO objeto de cotação. Pedidos antigos continuam com o objeto antigo.
    public void alterarCotacao(double cotacao10k, double cotacao18k) {
        cotacaoAtual = new CotacaoOuro(cotacao10k, cotacao18k);
    }

    public Optional<CotacaoOuro> getCotacaoAtual() {
        return Optional.ofNullable(cotacaoAtual);
    }

    // ==================== CLIENTES ====================

    public void adicionarCliente(Cliente cliente){
        if (buscarPorCpf(cliente.getCpf(), cliente.getLoja()) != null) {
            throw new IllegalArgumentException("Já existe um cliente com este CPF nesta loja.");
        }
        clientes.add(cliente);
    }

    // Busca apenas entre os clientes da loja informada (clientes são separados por loja).
    public Cliente buscarPorCpf(String cpf, Loja loja){
        for (Cliente c : clientes){
            if (c.pertenceA(loja) && c.getCpf().equals(cpf.trim())){
                return c;
            }
        }
        return null;
    }

    public List<Cliente> listarClientes(Loja loja) {
        List<Cliente> clientesDaLoja = new ArrayList<>();
        for (Cliente c : clientes) {
            if (c.pertenceA(loja)) {
                clientesDaLoja.add(c);
            }
        }
        return clientesDaLoja;
    }

    // Visão geral: somente para o ADMIN.
    public List<Cliente> listarTodosClientes() {
        return List.copyOf(clientes);
    }

    // ==================== PEDIDOS ====================

    public int gerarIdPedido() {
        return proximoIdPedido++;
    }

    // Cria um pedido (ainda não registrado) congelando a cotação vigente.
    public PedidoVenda novoPedido(Loja loja, Cliente cliente, String vendedor, String dataVenda, String dataEntrega) {
        if (cotacaoAtual == null) {
            throw new IllegalStateException("A cotação do ouro ainda não foi cadastrada pelo ADMIN.");
        }
        return new PedidoVenda(gerarIdPedido(), loja, cliente, vendedor, dataVenda, dataEntrega, cotacaoAtual);
    }

    public void adicionarPedido(PedidoVenda pedidoVenda) {
        pedidoVenda.confirmarLancamento();
        pedidos.add(pedidoVenda);
    }

    // Busca geral (ADMIN e FÁBRICA).
    public PedidoVenda buscarPedido(int id) {
        for (PedidoVenda p : pedidos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // Busca restrita aos pedidos da loja.
    public PedidoVenda buscarPedido(int id, Loja loja) {
        PedidoVenda pedido = buscarPedido(id);
        if (pedido != null && pedido.pertenceA(loja)) {
            return pedido;
        }
        return null;
    }

    public List<PedidoVenda> listarPedidos(Loja loja) {
        List<PedidoVenda> pedidosDaLoja = new ArrayList<>();
        for (PedidoVenda p : pedidos) {
            if (p.pertenceA(loja)) {
                pedidosDaLoja.add(p);
            }
        }
        return pedidosDaLoja;
    }

    public List<PedidoVenda> getPedidos() {
        return List.copyOf(pedidos);
    }

    // ==================== FATURAMENTO ====================
    // Soma dos valores finais das vendas válidas; CANCELADAS não contam.

    public double calcularFaturamento(Loja loja) {
        double total = 0;
        for (PedidoVenda p : listarPedidos(loja)) {
            if (p.contabilizaNoFaturamento()) {
                total += p.calcularValorFinal();
            }
        }
        return total;
    }

    public double calcularFaturamentoTotal() {
        double total = 0;
        for (Loja loja : lojas) {
            total += calcularFaturamento(loja);
        }
        return total;
    }
}
