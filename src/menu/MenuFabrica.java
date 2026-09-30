package menu;

import impressao.Formatacao;
import impressao.ImpressaoPedido;
import sistema.SistemaShowroom;
import venda.PedidoVenda;
import venda.StatusPedido;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuFabrica {

    private Entrada entrada;
    private SistemaShowroom sistema;

    public MenuFabrica(Scanner scanner, SistemaShowroom sistema) {
        this.entrada = new Entrada(scanner);
        this.sistema = sistema;
    }

    public void iniciar() {

        while (true) {

            System.out.println("\n========== MENU FÁBRICA ==========");
            System.out.println("1 - Listar pedidos");
            System.out.println("2 - Imprimir pedido");
            System.out.println("3 - Finalizar pedido");
            System.out.println("4 - Status geral dos pedidos");
            System.out.println("0 - Sair");

            String opcao = entrada.lerTexto("Escolha uma opção: ");

            if (opcao.equals("0")) {
                System.out.println("Saindo da conta...");
                return;
            }

            try {
                switch (opcao) {
                    case "1" -> listarPedidos();
                    case "2" -> imprimirPedido();
                    case "3" -> finalizarPedido();
                    case "4" -> exibirStatusGeral();
                    default -> System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Não foi possível concluir: " + e.getMessage());
            }
        }
    }

    private void listarPedidos() {
        System.out.println("\n========== PEDIDOS DAS LOJAS ==========");
        List<PedidoVenda> pedidos = sistema.getPedidos();
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido lançado.");
            return;
        }
        for (PedidoVenda p : pedidos) {
            System.out.println("Pedido " + p.getId() + " | " + p.getLoja().getNome()
                    + " | " + p.getStatus() + (p.isEditado() ? " | *Editado*" : "")
                    + " | Data prometida: " + p.getDataEntrega());
        }
    }

    // Imprimir muda o status automaticamente para OK (REGRAS_NEGOCIO.md, seção 10).
    private void imprimirPedido() {
        PedidoVenda pedido = buscarPedido();
        if (pedido == null) {
            return;
        }
        pedido.registrarImpressaoFabrica();
        System.out.println();
        System.out.println(ImpressaoPedido.gerarViaFabrica(pedido));
    }

    private void finalizarPedido() {
        PedidoVenda pedido = buscarPedido();
        if (pedido == null) {
            return;
        }
        if (!pedido.podeSerFinalizado()) {
            System.out.println("Só é possível finalizar um pedido com status OK (impresso pela fábrica). "
                    + "Status atual: " + pedido.getStatus() + ".");
            return;
        }
        Double pesoFinal = entrada.lerDecimalPositivoOpcional("Peso final produzido em g (Enter para não informar): ");
        pedido.finalizar(pesoFinal);

        System.out.println("Pedido " + pedido.getId() + " FINALIZADO.");
        if (pesoFinal != null) {
            System.out.println("Peso final registrado: " + Formatacao.peso(pesoFinal));
        }
    }

    private void exibirStatusGeral() {
        System.out.println("\n========== STATUS GERAL ==========");
        Map<StatusPedido, Integer> quantidades = new EnumMap<>(StatusPedido.class);
        for (StatusPedido status : StatusPedido.values()) {
            quantidades.put(status, 0);
        }
        for (PedidoVenda p : sistema.getPedidos()) {
            quantidades.put(p.getStatus(), quantidades.get(p.getStatus()) + 1);
        }
        for (Map.Entry<StatusPedido, Integer> item : quantidades.entrySet()) {
            System.out.println(item.getKey() + ": " + item.getValue());
        }
    }

    private PedidoVenda buscarPedido() {
        int id = entrada.lerInteiro("Número do pedido: ");
        PedidoVenda pedido = sistema.buscarPedido(id);
        if (pedido == null) {
            System.out.println("Pedido não encontrado.");
        }
        return pedido;
    }
}
