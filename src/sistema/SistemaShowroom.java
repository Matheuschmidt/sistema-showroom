package sistema;

import catalogo.CatalogoModelos;
import dominio.cliente.Cliente;
import venda.PedidoVenda;

import java.util.ArrayList;
import java.util.List;

public class SistemaShowroom {
    private CatalogoModelos catalogoModelos = new CatalogoModelos();

    public CatalogoModelos getCatalogoModelos() {
        return catalogoModelos;
    }

    private List<PedidoVenda> pedidos = new ArrayList<>();
    private int proximoIdPedido = 1;

    private List<Cliente> clientes = new ArrayList<>();

    public int gerarIdPedido() {
        return proximoIdPedido++;
    }

    public void adicionarPedido(PedidoVenda pedidoVenda) {
        pedidos.add(pedidoVenda);
    }

    public void adicionarCliente(Cliente cliente){
        clientes.add(cliente);
    }

    public Cliente buscarPorCpf(String cpf){
        for (Cliente c : clientes){
            if (cpf.equals(c.getCpf())){
                return c;
            }
        }
        return null;
    }

    public List<PedidoVenda> getPedidos() {
        return pedidos;
    }
}