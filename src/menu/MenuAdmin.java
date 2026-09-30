package menu;

import dominio.cliente.Cliente;
import dominio.produto.Modelo;
import dominio.usuario.Loja;
import impressao.Formatacao;
import sistema.SistemaShowroom;
import venda.CotacaoOuro;
import venda.PedidoVenda;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuAdmin {

    private Entrada entrada;
    private SistemaShowroom sistema;

    public MenuAdmin(Scanner scanner, SistemaShowroom sistema) {
        this.entrada = new Entrada(scanner);
        this.sistema = sistema;
    }

    public void iniciar() {

        while (true) {

            System.out.println("\n========== MENU ADMIN ==========");
            System.out.println("1 - Cadastrar cotação do ouro");
            System.out.println("2 - Visualizar faturamento total");
            System.out.println("3 - Visualizar faturamento por loja");
            System.out.println("4 - Listar clientes");
            System.out.println("5 - Listar modelos");
            System.out.println("6 - Ver status dos pedidos");
            System.out.println("0 - Sair");

            String opcao = entrada.lerTexto("Escolha uma opção: ");

            if (opcao.equals("0")) {
                System.out.println("Saindo da conta...");
                return;
            }

            try {
                switch (opcao) {
                    case "1" -> cadastrarCotacao();
                    case "2" -> exibirFaturamentoTotal();
                    case "3" -> exibirFaturamentoPorLoja();
                    case "4" -> listarClientes();
                    case "5" -> listarModelos();
                    case "6" -> listarStatusPedidos();
                    default -> System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Não foi possível concluir: " + e.getMessage());
            }
        }
    }

    private void cadastrarCotacao() {
        System.out.println("\n========== COTAÇÃO DO OURO ==========");

        Optional<CotacaoOuro> atual = sistema.getCotacaoAtual();
        if (atual.isPresent()) {
            System.out.println("Cotação atual 10K: " + Formatacao.moeda(atual.get().getCotacao10k()) + "/g");
            System.out.println("Cotação atual 18K: " + Formatacao.moeda(atual.get().getCotacao18k()) + "/g");
        } else {
            System.out.println("Nenhuma cotação cadastrada.");
        }

        double cotacao10k = entrada.lerDecimalPositivo("Nova cotação 10K (R$/g): ");
        double cotacao18k = entrada.lerDecimalPositivo("Nova cotação 18K (R$/g): ");
        sistema.alterarCotacao(cotacao10k, cotacao18k);

        System.out.println("Cotação atualizada. Ela vale somente para novas vendas.");
    }

    private void exibirFaturamentoTotal() {
        System.out.println("\n========== FATURAMENTO TOTAL ==========");
        System.out.println("Total das lojas: " + Formatacao.moeda(sistema.calcularFaturamentoTotal()));
        System.out.println("(pedidos cancelados não são contabilizados)");
    }

    private void exibirFaturamentoPorLoja() {
        System.out.println("\n========== FATURAMENTO POR LOJA ==========");
        for (Loja loja : sistema.getLojas()) {
            System.out.println(loja.getNome() + ": " + Formatacao.moeda(sistema.calcularFaturamento(loja)));
        }
        System.out.println("(pedidos cancelados não são contabilizados)");
    }

    private void listarClientes() {
        System.out.println("\n========== CLIENTES (TODAS AS LOJAS) ==========");
        List<Cliente> clientes = sistema.listarTodosClientes();
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        for (Cliente c : clientes) {
            System.out.println(c.getLoja().getNome() + " | " + c.getNome() + " | CPF: " + c.getCpf()
                    + " | " + c.getTelefone() + " | " + c.getEndereco() + " - " + c.getCidade());
        }
    }

    private void listarModelos() {
        System.out.println("\n========== MODELOS ==========");
        for (Modelo m : sistema.getCatalogoModelos().listarModelos()) {
            System.out.println(m.getReferencia()
                    + " | Largura: " + Formatacao.numero(m.getLargura()) + " mm"
                    + " | Peso unidade 10K: " + Formatacao.peso(m.getPesoBase10k())
                    + " | Peso unidade 18K: " + Formatacao.peso(m.getPesoBase18k()));
        }
    }

    private void listarStatusPedidos() {
        System.out.println("\n========== STATUS DOS PEDIDOS ==========");
        List<PedidoVenda> pedidos = sistema.getPedidos();
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
            return;
        }
        for (PedidoVenda p : pedidos) {
            System.out.println("Pedido " + p.getId() + " | " + p.getLoja().getNome()
                    + " | " + p.getStatus() + (p.isEditado() ? " | *Editado*" : "")
                    + " | " + Formatacao.moeda(p.calcularValorFinal()));
        }
    }
}
