package menu;

import dominio.cliente.Cliente;
import dominio.produto.Alianca;
import dominio.produto.Modelo;
import dominio.produto.TeorOuro;
import dominio.produto.TipoAro;
import dominio.usuario.Loja;
import impressao.Formatacao;
import impressao.ImpressaoPedido;
import sistema.SistemaShowroom;
import venda.Acrescimo;
import venda.PedidoVenda;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuLoja {

    private Entrada entrada;
    private Loja loja;
    private SistemaShowroom sistema;

    public MenuLoja(Scanner scanner) {
        this.entrada = new Entrada(scanner);
    }

    public void iniciar(Loja loja, SistemaShowroom sistema) {
        this.loja = loja;
        this.sistema = sistema;

        while (true) {

            System.out.println("\n========== MENU LOJA ==========");
            System.out.println("Loja: " + loja.getNome());
            System.out.println("1 - Criar pedido");
            System.out.println("2 - Editar pedido");
            System.out.println("3 - Listar pedidos");
            System.out.println("4 - Imprimir pedido para o cliente");
            System.out.println("5 - Marcar pedido como entregue");
            System.out.println("6 - Cancelar pedido");
            System.out.println("7 - Criar cliente");
            System.out.println("8 - Editar cliente");
            System.out.println("9 - Listar clientes");
            System.out.println("10 - Faturamento da loja");
            System.out.println("0 - Sair");

            String opcao = entrada.lerTexto("Escolha uma opção: ");

            if (opcao.equals("0")) {
                System.out.println("Saindo da conta...");
                return;
            }

            try {
                switch (opcao) {
                    case "1" -> criarPedido();
                    case "2" -> editarPedido();
                    case "3" -> listarPedidos();
                    case "4" -> imprimirPedidoCliente();
                    case "5" -> marcarEntregue();
                    case "6" -> cancelarPedido();
                    case "7" -> criarCliente();
                    case "8" -> editarCliente();
                    case "9" -> listarClientes();
                    case "10" -> exibirFaturamento();
                    default -> System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Não foi possível concluir: " + e.getMessage());
            }
        }
    }

    // ==================== PEDIDOS ====================

    private void criarPedido() {
        System.out.println("\n========== CRIAR PEDIDO ==========");

        if (sistema.getCotacaoAtual().isEmpty()) {
            System.out.println("A cotação do ouro ainda não foi cadastrada pelo ADMIN. "
                    + "Não é possível criar pedidos.");
            return;
        }

        // CLIENTE
        Cliente cliente = obterOuCadastrarCliente();

        // UNIDADES
        Alianca unidade1 = lerUnidade(1);
        Alianca unidade2 = null;
        if (entrada.lerSimNao("\nAdicionar segunda unidade? (S/N): ")) {
            unidade2 = lerUnidade(2);
        }

        // DADOS DA VENDA
        System.out.println();
        double percentualAlteracaoPeso = lerPercentualAlteracaoPeso(0);
        String vendedor = entrada.lerTextoObrigatorio("Nome do vendedor: ");
        String dataVenda = entrada.lerTextoObrigatorio("Data da venda: ");
        String dataEntrega = entrada.lerTextoObrigatorio("Data prometida ao cliente: ");

        // VALORES
        List<Acrescimo> acrescimos = lerAcrescimos();
        double desconto = lerDesconto(0);

        // CRIA PEDIDO (a cotação vigente é congelada aqui)
        PedidoVenda pedido = sistema.novoPedido(loja, cliente, vendedor, dataVenda, dataEntrega);
        pedido.adicionarAlianca(unidade1);
        if (unidade2 != null) {
            pedido.adicionarAlianca(unidade2);
        }
        pedido.definirPercentualAlteracaoPeso(percentualAlteracaoPeso);
        for (Acrescimo acrescimo : acrescimos) {
            pedido.adicionarAcrescimo(acrescimo);
        }
        pedido.aplicarDesconto(desconto);

        // SALVA PEDIDO
        sistema.adicionarPedido(pedido);

        System.out.println("\nPedido criado com sucesso!");
        System.out.println("Número do pedido: " + pedido.getId());
        System.out.println("Valor da venda: " + Formatacao.moeda(pedido.calcularValorFinal()));
    }

    private void editarPedido() {
        System.out.println("\n========== EDITAR PEDIDO ==========");
        PedidoVenda pedido = buscarPedidoDaLoja();
        if (pedido == null) {
            return;
        }
        if (!pedido.podeSerEditado()) {
            System.out.println("O pedido está " + pedido.getStatus() + " e não pode mais ser editado.");
            return;
        }

        System.out.println("(Enter mantém o valor atual)");

        String vendedor = entrada.lerTextoComPadrao("Vendedor", pedido.getVendedor());
        String dataVenda = entrada.lerTextoComPadrao("Data da venda", pedido.getDataVenda());
        String dataEntrega = entrada.lerTextoComPadrao("Data prometida ao cliente", pedido.getDataEntrega());
        pedido.editarDadosVenda(vendedor, dataVenda, dataEntrega);

        List<Alianca> unidades = pedido.getUnidades();
        for (int i = 0; i < unidades.size(); i++) {
            int numero = i + 1;
            if (entrada.lerSimNao("\nEditar unidade " + numero + "? (S/N): ")) {
                pedido.editarUnidade(numero, lerUnidadeComPadrao(numero, unidades.get(i)));
            }
        }
        if (unidades.size() == 1 && entrada.lerSimNao("\nAdicionar segunda unidade? (S/N): ")) {
            pedido.adicionarAlianca(lerUnidade(2));
        }

        System.out.println();
        pedido.definirPercentualAlteracaoPeso(lerPercentualAlteracaoPeso(pedido.getPercentualAlteracaoPeso()));

        if (entrada.lerSimNao("Refazer os acréscimos? (S/N): ")) {
            pedido.removerAcrescimos();
            for (Acrescimo acrescimo : lerAcrescimos()) {
                pedido.adicionarAcrescimo(acrescimo);
            }
        }
        pedido.aplicarDesconto(lerDesconto(pedido.getDesconto()));

        System.out.println("\nPedido atualizado.");
        System.out.println("Valor da venda: " + Formatacao.moeda(pedido.calcularValorFinal()));
    }

    private void listarPedidos() {
        System.out.println("\n========== PEDIDOS ==========");

        List<PedidoVenda> pedidos = sistema.listarPedidos(loja);
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado para esta loja.");
            return;
        }

        for (PedidoVenda p : pedidos) {
            System.out.println("-----------------------------");
            System.out.println("Pedido Nº: " + p.getId());
            System.out.println("Status: " + p.getStatus());
            System.out.println("Cliente: " + p.getCliente().getNome());
            System.out.println("Vendedor: " + p.getVendedor());
            System.out.println("Data da venda: " + p.getDataVenda());
            System.out.println("Data prometida: " + p.getDataEntrega());
            System.out.println("Valor: " + Formatacao.moeda(p.calcularValorFinal()));
        }
    }

    private void imprimirPedidoCliente() {
        PedidoVenda pedido = buscarPedidoDaLoja();
        if (pedido != null) {
            System.out.println();
            System.out.println(ImpressaoPedido.gerarViaCliente(pedido));
        }
    }

    private void marcarEntregue() {
        PedidoVenda pedido = buscarPedidoDaLoja();
        if (pedido != null) {
            pedido.marcarEntregue();
            System.out.println("Pedido " + pedido.getId() + " marcado como ENTREGUE.");
        }
    }

    private void cancelarPedido() {
        PedidoVenda pedido = buscarPedidoDaLoja();
        if (pedido == null) {
            return;
        }
        if (entrada.lerSimNao("Confirmar cancelamento do pedido " + pedido.getId() + "? (S/N): ")) {
            pedido.cancelar();
            System.out.println("Pedido " + pedido.getId() + " CANCELADO.");
        }
    }

    // ==================== CLIENTES ====================

    private void criarCliente() {
        System.out.println("\n========== CRIAR CLIENTE ==========");
        String cpf = entrada.lerTextoObrigatorio("CPF do cliente: ");
        if (sistema.buscarPorCpf(cpf, loja) != null) {
            System.out.println("Já existe um cliente com este CPF nesta loja.");
            return;
        }
        cadastrarCliente(cpf);
    }

    private void editarCliente() {
        System.out.println("\n========== EDITAR CLIENTE ==========");
        String cpf = entrada.lerTextoObrigatorio("CPF do cliente: ");
        Cliente cliente = sistema.buscarPorCpf(cpf, loja);
        if (cliente == null) {
            System.out.println("Cliente não encontrado nesta loja.");
            return;
        }

        System.out.println("(Enter mantém o valor atual)");
        String nome = entrada.lerTextoComPadrao("Nome", cliente.getNome());
        String telefone = entrada.lerTextoComPadrao("Telefone", cliente.getTelefone());
        String endereco = entrada.lerTextoComPadrao("Endereço", cliente.getEndereco());
        String cidade = entrada.lerTextoComPadrao("Cidade", cliente.getCidade());

        cliente.alterarDados(nome, telefone, endereco, cidade);
        System.out.println("Cliente atualizado.");
    }

    private void listarClientes() {
        System.out.println("\n========== CLIENTES ==========");
        List<Cliente> clientes = sistema.listarClientes(loja);
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado para esta loja.");
            return;
        }
        for (Cliente c : clientes) {
            System.out.println(c.getNome() + " | CPF: " + c.getCpf() + " | " + c.getTelefone()
                    + " | " + c.getEndereco() + " - " + c.getCidade());
        }
    }

    private Cliente obterOuCadastrarCliente() {
        String cpf = entrada.lerTextoObrigatorio("CPF do cliente: ");
        Cliente cliente = sistema.buscarPorCpf(cpf, loja);

        if (cliente == null) {
            System.out.println("\nCliente não encontrado.");
            System.out.println("Vamos cadastrar um novo cliente.");
            return cadastrarCliente(cpf);
        }

        System.out.println("\nCliente encontrado: " + cliente.getNome());
        return cliente;
    }

    private Cliente cadastrarCliente(String cpf) {
        String nome = entrada.lerTextoObrigatorio("Nome: ");
        String telefone = entrada.lerTexto("Telefone: ");
        String endereco = entrada.lerTexto("Endereço: ");
        String cidade = entrada.lerTexto("Cidade: ");

        Cliente cliente = new Cliente(nome, cpf, telefone, endereco, cidade, loja);
        sistema.adicionarCliente(cliente);

        System.out.println("Cliente cadastrado com sucesso!");
        return cliente;
    }

    // ==================== FATURAMENTO ====================

    private void exibirFaturamento() {
        System.out.println("\n========== FATURAMENTO ==========");
        System.out.println(loja.getNome() + ": " + Formatacao.moeda(sistema.calcularFaturamento(loja)));
        System.out.println("(pedidos cancelados não são contabilizados)");
    }

    // ==================== AUXILIARES ====================

    private PedidoVenda buscarPedidoDaLoja() {
        int id = entrada.lerInteiro("Número do pedido: ");
        PedidoVenda pedido = sistema.buscarPedido(id, loja);
        if (pedido == null) {
            System.out.println("Pedido não encontrado nesta loja.");
        }
        return pedido;
    }

    private Alianca lerUnidade(int numero) {
        System.out.println("\n--- Unidade " + numero + " ---");

        Modelo modelo = lerModelo();
        TipoAro tipoAro = entrada.lerTipoAro("Tipo de aro (F/M): ");
        double aro = entrada.lerDecimalPositivo("Aro: ");
        String gravacao = entrada.lerTexto("Gravação: ");
        TeorOuro teorOuro = entrada.lerTeor("Teor do ouro (10/18): ");
        Double larguraPE = entrada.lerDecimalPositivoOpcional("P.E. - largura desejada em mm (original "
                + Formatacao.numero(modelo.getLargura()) + " mm; Enter se não houver): ");
        boolean maisAnatomica = entrada.lerSimNao("Mais anatômica? (S/N): ");
        if (maisAnatomica) {
            avisarMaisAnatomica();
        }

        return new Alianca(modelo, tipoAro, aro, gravacao, teorOuro, larguraPE, maisAnatomica);
    }

    private Alianca lerUnidadeComPadrao(int numero, Alianca atual) {
        System.out.println("--- Unidade " + numero + " ---");

        Modelo modelo = lerModeloComPadrao(atual.getModelo());
        TipoAro tipoAro = entrada.lerTipoAroComPadrao("Tipo de aro (F/M)", atual.getTipoAro());
        double aro = entrada.lerDecimalComPadrao("Aro", atual.getAro());
        String gravacao = entrada.lerTextoComPadrao("Gravação", atual.getGravacao());
        TeorOuro teorOuro = entrada.lerTeorComPadrao("Teor do ouro (10/18)", atual.getTeorOuro());
        Double larguraPE = entrada.lerPEComPadrao("P.E. - largura desejada em mm (original "
                + Formatacao.numero(modelo.getLargura()) + " mm)", atual.getLarguraPE());
        boolean maisAnatomica = entrada.lerSimNaoComPadrao("Mais anatômica? (S/N)", atual.isMaisAnatomica());
        if (maisAnatomica) {
            avisarMaisAnatomica();
        }

        return new Alianca(modelo, tipoAro, aro, gravacao, teorOuro, larguraPE, maisAnatomica);
    }

    private Modelo lerModelo() {
        while (true) {
            String referencia = entrada.lerTextoObrigatorio("Referência do modelo: ").toUpperCase();
            Modelo modelo = sistema.getCatalogoModelos().buscarModelo(referencia);
            if (modelo != null) {
                System.out.println("Modelo selecionado: " + modelo.getReferencia());
                return modelo;
            }
            System.out.println("Modelo não encontrado.");
        }
    }

    private Modelo lerModeloComPadrao(Modelo atual) {
        while (true) {
            String referencia = entrada.lerTextoComPadrao("Referência do modelo", atual.getReferencia())
                    .toUpperCase();
            Modelo modelo = sistema.getCatalogoModelos().buscarModelo(referencia);
            if (modelo != null) {
                return modelo;
            }
            System.out.println("Modelo não encontrado.");
        }
    }

    private double lerPercentualAlteracaoPeso(double atual) {
        while (true) {
            double percentual = entrada.lerDecimalComPadrao("Alteração de peso da venda (%)", atual);
            if (percentual > -100) {
                return percentual;
            }
            System.out.println("A alteração de peso deve ser maior que -100%.");
        }
    }

    private List<Acrescimo> lerAcrescimos() {
        List<Acrescimo> acrescimos = new ArrayList<>();
        double percentual = entrada.lerDecimalNaoNegativo("Acréscimo percentual (%) (0 se não houver): ");
        double valorFixo = entrada.lerDecimalNaoNegativo("Acréscimo em R$ (0 se não houver): ");
        if (percentual > 0) {
            acrescimos.add(new Acrescimo(percentual, 0));
        }
        if (valorFixo > 0) {
            acrescimos.add(new Acrescimo(0, valorFixo));
        }
        return acrescimos;
    }

    private double lerDesconto(double atual) {
        while (true) {
            double desconto = entrada.lerDecimalComPadrao("Desconto (%)", atual);
            if (desconto >= 0 && desconto <= 100) {
                return desconto;
            }
            System.out.println("O desconto deve estar entre 0% e 100%.");
        }
    }

    private void avisarMaisAnatomica() {
        System.out.println("Atenção: o percentual de \"mais anatômica\" ainda não foi definido nas regras.");
        System.out.println("Ele ainda NÃO é aplicado automaticamente no valor nem no peso de produção.");
    }
}
