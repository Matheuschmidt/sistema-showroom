package menu;

import dominio.cliente.Cliente;
import dominio.produto.Modelo;
import dominio.usuario.Loja;
import sistema.SistemaShowroom;
import venda.PedidoVenda;

import java.util.Scanner;

public class MenuLoja {

    private Scanner scanner;

    public MenuLoja(Scanner scanner) {
        this.scanner = scanner;
    }

    public void iniciar(Loja loja, SistemaShowroom sistema) {

        while (true) {

            System.out.println("\n========== MENU LOJA ==========");
            System.out.println("Loja: " + loja.getNome());
            System.out.println("1 - Criar pedido");
            System.out.println("2 - Editar pedido");
            System.out.println("3 - Listar pedidos");
            System.out.println("4 - Criar cliente");
            System.out.println("5 - Editar cliente");
            System.out.println("0 - Sair");

            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {

                case "1":
                    System.out.println("\n========== CRIAR PEDIDO ==========");

                    // CLIENTE
                    System.out.print("CPF do cliente: ");
                    String cpf = scanner.nextLine();

                    Cliente cliente = sistema.buscarPorCpf(cpf);

                    if (cliente == null) {

                        System.out.println("\nCliente não encontrado.");
                        System.out.println("Vamos cadastrar um novo cliente.");

                        System.out.print("Nome: ");
                        String nome = scanner.nextLine();

                        System.out.print("Telefone: ");
                        String telefone = scanner.nextLine();

                        System.out.print("Endereço: ");
                        String endereco = scanner.nextLine();

                        cliente = new Cliente(
                                nome,
                                cpf,
                                telefone,
                                endereco
                        );

                        sistema.adicionarCliente(cliente);

                        System.out.println("Cliente cadastrado com sucesso!");

                    } else {

                        System.out.println(
                                "\nCliente encontrado: "
                                        + cliente.getNome()
                        );
                    }

                    // MODELO
                    System.out.print("\nReferência do modelo: ");
                    String referenciaModelo = scanner.nextLine();

                    Modelo modelo = sistema
                            .getCatalogoModelos()
                            .buscarModelo(referenciaModelo);

                    if (modelo == null) {
                        System.out.println("Modelo não encontrado.");
                        break;
                    }

                    System.out.println(
                            "Modelo selecionado: "
                                    + modelo.getReferencia()
                    );

                    // DADOS DA ALIANÇA
                    System.out.print("Tipo de aro (F/M): ");
                    String tipoAro = scanner.nextLine();

                    System.out.print("Aro: ");
                    double aro = Double.parseDouble(scanner.nextLine());

                    System.out.print("Gravação: ");
                    String gravacao = scanner.nextLine();

                    System.out.print("Teor do ouro (10/18): ");
                    int teorOuro = Integer.parseInt(scanner.nextLine());

                    System.out.print("Alteração de peso (%): ");
                    double percentualAlteracaoPeso =
                            Double.parseDouble(scanner.nextLine());

                    // DADOS DA VENDA
                    System.out.print("Nome do vendedor: ");
                    String vendedor = scanner.nextLine();

                    System.out.print("Data da venda: ");
                    String dataVenda = scanner.nextLine();

                    System.out.print("Data de entrega: ");
                    String dataEntrega = scanner.nextLine();

                    // CRIA PEDIDO
                    int idPedido = sistema.gerarIdPedido();

                    PedidoVenda pedido = new PedidoVenda(
                            idPedido,
                            loja,
                            vendedor,
                            dataVenda,
                            dataEntrega
                    );

                    // DEFINE CLIENTE
                    pedido.definirCliente(cliente);

                    // ADICIONA ALIANÇA
                    pedido.adicionarAlianca(
                            modelo,
                            tipoAro,
                            aro,
                            gravacao,
                            teorOuro,
                            percentualAlteracaoPeso
                    );

                    // SALVA PEDIDO
                    sistema.adicionarPedido(pedido);

                    System.out.println("\nPedido criado com sucesso!");
                    System.out.println(
                            "Número do pedido: "
                                    + pedido.getId()
                    );

                    break;

                case "2":
                    System.out.println("\nEditar pedido");
                    System.out.println("Funcionalidade em desenvolvimento.");
                    break;

                case "3":
                    System.out.println("\n========== PEDIDOS ==========");

                    boolean encontrouPedido = false;

                    for (PedidoVenda p : sistema.getPedidos()) {

                        if (p.getLoja() == loja) {

                            encontrouPedido = true;

                            System.out.println("-----------------------------");
                            System.out.println(
                                    "Pedido Nº: " + p.getId()
                            );
                            System.out.println(
                                    "Cliente: "
                                            + p.getCliente().getNome()
                            );
                            System.out.println(
                                    "Vendedor: "
                                            + p.getVendedor()
                            );
                            System.out.println(
                                    "Data da venda: "
                                            + p.getDataVenda()
                            );
                            System.out.println(
                                    "Data de entrega: "
                                            + p.getDataEntrega()
                            );
                        }
                    }

                    if (!encontrouPedido) {
                        System.out.println(
                                "Nenhum pedido cadastrado para esta loja."
                        );
                    }

                    break;

                case "4":
                    System.out.println("\nCriar cliente");
                    System.out.println("Funcionalidade em desenvolvimento.");
                    break;

                case "5":
                    System.out.println("\nEditar cliente");
                    System.out.println("Funcionalidade em desenvolvimento.");
                    break;

                case "0":
                    System.out.println("Saindo da conta...");
                    return;

                default:
                    System.out.println("Opção inválida.");
            }
        }
    }
}