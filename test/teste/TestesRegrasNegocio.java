package teste;

import calculo.CalculadoraPesoProducao;
import calculo.IndiceAro;
import dominio.cliente.Cliente;
import dominio.produto.Alianca;
import dominio.produto.Modelo;
import dominio.produto.TeorOuro;
import dominio.produto.TipoAro;
import dominio.usuario.Admin;
import dominio.usuario.Loja;
import impressao.ImpressaoPedido;
import menu.Entrada;
import sistema.SistemaShowroom;
import venda.Acrescimo;
import venda.PedidoVenda;
import venda.StatusPedido;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Testes das regras de REGRAS_NEGOCIO.md, sem framework (apenas Java puro).
// Execução: rodar o main desta classe. Termina com código 1 se algum teste falhar.
public class TestesRegrasNegocio {

    private static final double TOLERANCIA = 1e-9;
    private static int aprovados = 0;
    private static final List<String> falhas = new ArrayList<>();

    // Modelos de teste com números fáceis de conferir à mão.
    // Modelo(referência, largura, aroBase, peso unidade 10K, peso unidade 18K)
    private static final Modelo MODELO_A = new Modelo("A", 5, 20, 1.20, 1.50);
    private static final Modelo MODELO_B = new Modelo("B", 4, 20, 1.40, 1.80);

    public static void main(String[] args) {
        testarAro();
        testarArredondamento();
        testarUnidades();
        testarTeor();
        testarPE();
        testarPesoProducao();
        testarMaisAnatomica();
        testarCotacao();
        testarValor();
        testarStatus();
        testarEdicao();
        testarFaturamento();
        testarClientes();
        testarEntrada();
        testarImpressao();
        testarLogin();

        System.out.println("\n==============================");
        System.out.println("Testes aprovados: " + aprovados);
        System.out.println("Testes com falha: " + falhas.size());
        for (String falha : falhas) {
            System.out.println("  FALHOU: " + falha);
        }
        System.out.println("==============================");

        if (!falhas.isEmpty()) {
            System.exit(1);
        }
    }

    // ==================== ARO ====================

    private static void testarAro() {
        IndiceAro indice = new IndiceAro();
        verificarIgual("Aro 20 usa índice do 20", 1.0000, indice.buscarIndice(20));
        verificarIgual("Aro 20,5 usa índice do 21", 1.0166, indice.buscarIndice(20.5));
        verificarIgual("Aro 21 usa índice do 21", 1.0166, indice.buscarIndice(21));
        verificarIgual("Aro 21,5 usa índice do 22 (valor oficial)", 1.0330, indice.buscarIndice(21.5));
        verificarIgual("Aro 7 (< 8) usa índice do 8", 0.8000, indice.buscarIndice(7));
        verificarIgual("Aro 3 (< 8) usa índice do 8", 0.8000, indice.buscarIndice(3));
        verificarIgual("Aro 41 (> 40) usa índice do 40", 1.3330, indice.buscarIndice(41));
        verificarIgual("Aro 40,5 (> 40) usa índice do 40", 1.3330, indice.buscarIndice(40.5));
    }

    // ==================== ARREDONDAMENTO ====================

    private static void testarArredondamento() {
        verificarIgual("2,01 → 2,05", 2.05, CalculadoraPesoProducao.arredondarParaCima(2.01));
        verificarIgual("2,03 → 2,05", 2.05, CalculadoraPesoProducao.arredondarParaCima(2.03));
        verificarIgual("2,049 → 2,05", 2.05, CalculadoraPesoProducao.arredondarParaCima(2.049));
        verificarIgual("2,05 → 2,05", 2.05, CalculadoraPesoProducao.arredondarParaCima(2.05));
        verificarIgual("2,051 → 2,10", 2.10, CalculadoraPesoProducao.arredondarParaCima(2.051));
        verificarIgual("2,07 → 2,10", 2.10, CalculadoraPesoProducao.arredondarParaCima(2.07));
        verificarIgual("2,11 → 2,15", 2.15, CalculadoraPesoProducao.arredondarParaCima(2.11));
        // 1.5 × 0.8 = 1.2000000000000002 no double; Math.ceil puro daria 1.25.
        verificarIgual("Erro de ponto flutuante (1,50 × 0,8) não sobe indevidamente",
                1.20, CalculadoraPesoProducao.arredondarParaCima(1.5 * 0.8));
    }

    // ==================== UNIDADES / PESO COMERCIAL ====================

    private static void testarUnidades() {
        Cenario c = new Cenario();

        PedidoVenda uma = c.pedido(c.caxias, unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18));
        verificarIgual("Uma unidade: peso comercial = 1,50", 1.50, uma.calcularPesoComercial());

        PedidoVenda iguais = c.pedido(c.caxias,
                unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18),
                unidade(MODELO_A, TipoAro.M, 22, TeorOuro.K18));
        verificarIgual("Duas unidades iguais: 1,50 + 1,50 = 3,00", 3.00, iguais.calcularPesoComercial());

        PedidoVenda diferentes = c.pedido(c.caxias,
                unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18),
                unidade(MODELO_B, TipoAro.M, 20, TeorOuro.K18));
        verificarIgual("Duas unidades diferentes: 1,50 + 1,80 = 3,30", 3.30, diferentes.calcularPesoComercial());

        verificarLancaExcecao("Não aceita terceira unidade", IllegalArgumentException.class,
                () -> diferentes.adicionarAlianca(unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18)));
    }

    // ==================== TEOR ====================

    private static void testarTeor() {
        Cenario c = new Cenario(); // cotação 10K = 300, 18K = 500

        PedidoVenda mesmoTeor = c.pedido(c.caxias,
                unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18),
                unidade(MODELO_A, TipoAro.M, 20, TeorOuro.K18));
        verificarIgual("Mesmo teor: 3,00 g × 500 = 1500", 1500, mesmoTeor.calcularValorBase());

        PedidoVenda teoresDiferentes = c.pedido(c.caxias,
                unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K10),
                unidade(MODELO_A, TipoAro.M, 20, TeorOuro.K18));
        verificarIgual("Teores diferentes: 1,20 × 300 + 1,50 × 500 = 1110", 1110, teoresDiferentes.calcularValorBase());
        verificarIgual("Teores diferentes: peso comercial 1,20 + 1,50 = 2,70", 2.70,
                teoresDiferentes.calcularPesoComercial());
    }

    // ==================== P.E. ====================

    private static void testarPE() {
        Cenario c = new Cenario();

        PedidoVenda semPE = c.pedido(c.caxias, unidadePE(MODELO_A, TipoAro.F, null));
        verificarIgual("Sem P.E.: produção 1,50", 1.50, semPE.calcularPesoProducaoTotal());

        PedidoVenda peMenor = c.pedido(c.caxias, unidadePE(MODELO_A, TipoAro.F, 4.5));
        verificarIgual("P.E. menor (5 → 4,5 mm): 1,50 ÷ 5 × 4,5 = 1,35", 1.35, peMenor.calcularPesoProducaoTotal());

        PedidoVenda peMaior = c.pedido(c.caxias, unidadePE(MODELO_A, TipoAro.F, 5.5));
        verificarIgual("P.E. maior (5 → 5,5 mm): 1,50 ÷ 5 × 5,5 = 1,65", 1.65, peMaior.calcularPesoProducaoTotal());

        // Equivalência com a regra do par: 3,00 ÷ 5 × 4,5 = 2,70 → 2,70 ÷ 2 = 1,35 por unidade.
        PedidoVenda parPE = c.pedido(c.caxias,
                unidadePE(MODELO_A, TipoAro.F, 4.5),
                unidadePE(MODELO_A, TipoAro.M, 4.5));
        verificarIgual("Equivalência com a regra do par (3,00 ÷ 5 × 4,5 = 2,70)", 2.70,
                parPE.calcularPesoProducaoTotal());

        PedidoVenda peDiferentes = c.pedido(c.caxias,
                unidadePE(MODELO_A, TipoAro.F, 4.5),
                unidadePE(MODELO_A, TipoAro.M, 5.5));
        verificarIgual("F e M com P.E. diferentes: unidade F = 1,35", 1.35,
                peDiferentes.calcularPesoProducao(peDiferentes.getUnidades().get(0)));
        verificarIgual("F e M com P.E. diferentes: unidade M = 1,65", 1.65,
                peDiferentes.calcularPesoProducao(peDiferentes.getUnidades().get(1)));
        verificar("Área única de P.E. descreve F e M: \"F 4,5 mm / M 5,5 mm\"",
                "F 4,5 mm / M 5,5 mm".equals(ImpressaoPedido.descreverPE(peDiferentes)));
        verificar("Sem P.E. não gera área de P.E.", ImpressaoPedido.descreverPE(semPE) == null);

        verificarIgual("P.E. não altera o peso comercial", 1.50, peMenor.calcularPesoComercial());
        verificarIgual("P.E. não altera o valor da venda", semPE.calcularValorBase(), peMenor.calcularValorBase());
    }

    // ==================== PESO DE PRODUÇÃO × COMERCIAL ====================

    private static void testarPesoProducao() {
        Cenario c = new Cenario();

        PedidoVenda aro8 = c.pedido(c.caxias, unidade(MODELO_A, TipoAro.F, 8, TeorOuro.K18));
        verificarIgual("Aro 8: 1,50 × 0,8 = 1,20 (sem subir para 1,25 por erro de double)", 1.20,
                aro8.calcularPesoProducaoTotal());

        PedidoVenda aroMaior = c.pedido(c.caxias, unidade(MODELO_A, TipoAro.F, 22, TeorOuro.K18));
        verificarIgual("Aro 22: 1,50 × 1,0330 = 1,5495 → 1,55", 1.55, aroMaior.calcularPesoProducaoTotal());

        // F aro 16: 1,50 × 0,9333 = 1,39995 → 1,40 | M aro 20: 1,50 → total 2,90
        PedidoVenda par = c.pedido(c.caxias,
                unidade(MODELO_A, TipoAro.F, 16, TeorOuro.K18),
                unidade(MODELO_A, TipoAro.M, 20, TeorOuro.K18));
        verificarIgual("Produção arredonda cada unidade e soma: 1,40 + 1,50 = 2,90", 2.90,
                par.calcularPesoProducaoTotal());
        verificarIgual("Peso comercial continua 3,00", 3.00, par.calcularPesoComercial());
        verificarIgual("Valor usa peso comercial (3,00 × 500), não o de produção", 1500, par.calcularValorBase());

        PedidoVenda comAlteracao = c.pedido(c.caxias, unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18));
        comAlteracao.definirPercentualAlteracaoPeso(10);
        verificarIgual("Alteração de 10%: 1,50 × 1,10 = 1,65", 1.65, comAlteracao.calcularPesoProducaoTotal());
        verificarIgual("Alteração de peso não altera o valor", 750, comAlteracao.calcularValorBase());

        // 1,50 × 1,10 = 1,65 → ÷ 5 × 4,5 = 1,485 → × 1,0166 (aro 21) = 1,509651 → 1,55
        PedidoVenda completo = c.pedido(c.caxias,
                new Alianca(MODELO_A, TipoAro.F, 21, "", TeorOuro.K18, 4.5, false));
        completo.definirPercentualAlteracaoPeso(10);
        verificarIgual("Ordem completa: alteração → P.E. → aro → arredondamento = 1,55", 1.55,
                completo.calcularPesoProducaoTotal());
    }

    // ==================== MAIS ANATÔMICA ====================

    private static void testarMaisAnatomica() {
        Cenario c = new Cenario();

        PedidoVenda nenhuma = c.pedido(c.caxias, anatomica(TipoAro.F, false), anatomica(TipoAro.M, false));
        PedidoVenda somenteF = c.pedido(c.caxias, anatomica(TipoAro.F, true), anatomica(TipoAro.M, false));
        PedidoVenda somenteM = c.pedido(c.caxias, anatomica(TipoAro.F, false), anatomica(TipoAro.M, true));
        PedidoVenda ambas = c.pedido(c.caxias, anatomica(TipoAro.F, true), anatomica(TipoAro.M, true));

        verificar("Nenhuma: F e M sem mais anatômica",
                !nenhuma.getUnidades().get(0).isMaisAnatomica() && !nenhuma.getUnidades().get(1).isMaisAnatomica());
        verificar("Somente F", somenteF.getUnidades().get(0).isMaisAnatomica()
                && !somenteF.getUnidades().get(1).isMaisAnatomica());
        verificar("Somente M", !somenteM.getUnidades().get(0).isMaisAnatomica()
                && somenteM.getUnidades().get(1).isMaisAnatomica());
        verificar("F e M", ambas.getUnidades().get(0).isMaisAnatomica()
                && ambas.getUnidades().get(1).isMaisAnatomica());

        verificar("Via fábrica (nenhuma) não indica mais anatômica",
                contar(ImpressaoPedido.gerarViaFabrica(nenhuma), "MAIS ANATÔMICA") == 0);
        verificar("Via fábrica (somente F) indica 1 unidade",
                contar(ImpressaoPedido.gerarViaFabrica(somenteF), "MAIS ANATÔMICA") == 1);
        verificar("Via fábrica (F e M) indica 2 unidades",
                contar(ImpressaoPedido.gerarViaFabrica(ambas), "MAIS ANATÔMICA") == 2);

        verificarIgual("Mais anatômica é independente de percentualAlteracaoPeso", 0,
                ambas.getPercentualAlteracaoPeso());
    }

    // ==================== COTAÇÃO ====================

    private static void testarCotacao() {
        Cenario c = new Cenario(); // 18K = 500

        PedidoVenda antigo = c.pedido(c.caxias,
                unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18),
                unidade(MODELO_A, TipoAro.M, 20, TeorOuro.K18));
        verificarIgual("Venda antiga com cotação 500: 1500", 1500, antigo.calcularValorBase());

        c.sistema.alterarCotacao(350, 550);

        verificarIgual("Após alterar cotação, venda antiga mantém 1500", 1500, antigo.calcularValorBase());
        verificarIgual("Venda antiga mantém cotação 18K registrada (500)", 500,
                antigo.getCotacaoOuro().getCotacao18k());

        PedidoVenda novo = c.pedido(c.caxias,
                unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18),
                unidade(MODELO_A, TipoAro.M, 20, TeorOuro.K18));
        verificarIgual("Nova venda usa cotação nova: 3,00 × 550 = 1650", 1650, novo.calcularValorBase());

        SistemaShowroom semCotacao = new SistemaShowroom();
        Loja loja = new Loja("LOJA", "l", "s");
        Cliente cliente = new Cliente("X", "1", "", "", "", loja);
        verificarLancaExcecao("Sem cotação cadastrada não cria pedido", IllegalStateException.class,
                () -> semCotacao.novoPedido(loja, cliente, "V", "01/01", "10/01"));
    }

    // ==================== VALOR ====================

    private static void testarValor() {
        Cenario c = new Cenario();
        PedidoVenda pedido = c.pedidoComValores(c.caxias);

        // base 1500 → +10% (150) → +R$ 50 → subtotal 1700 → -5% (85) → 1615
        verificarIgual("Valor base = 1500", 1500, pedido.calcularValorBase());
        verificarIgual("Acréscimo percentual (10% da base) = 150", 150, pedido.calcularAcrescimoPercentual());
        verificarIgual("Acréscimo em R$ = 50", 50, pedido.calcularAcrescimoFixo());
        verificarIgual("Desconto de 5% sobre o subtotal 1700 → valor final 1615", 1615, pedido.calcularValorFinal());

        verificarLancaExcecao("Desconto acima de 100% é rejeitado", IllegalArgumentException.class,
                () -> pedido.aplicarDesconto(150));
        verificarLancaExcecao("Acréscimo negativo é rejeitado", IllegalArgumentException.class,
                () -> new Acrescimo(-5, 0));
    }

    // ==================== STATUS ====================

    private static void testarStatus() {
        Cenario c = new Cenario();
        PedidoVenda pedido = c.pedido(c.caxias, unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18));

        verificar("Pedido criado: PEDIDO CRIADO", pedido.getStatus() == StatusPedido.PEDIDO_CRIADO);

        verificarLancaExcecao("Não finaliza antes da fábrica imprimir", IllegalStateException.class,
                () -> pedido.finalizar(null));
        verificarLancaExcecao("Não marca ENTREGUE antes de FINALIZADO", IllegalStateException.class,
                pedido::marcarEntregue);

        pedido.registrarImpressaoFabrica();
        verificar("Fábrica imprime → OK", pedido.getStatus() == StatusPedido.OK);
        pedido.registrarImpressaoFabrica();
        verificar("Reimpressão mantém OK", pedido.getStatus() == StatusPedido.OK);

        pedido.finalizar(1.55);
        verificar("Fábrica finaliza → FINALIZADO", pedido.getStatus() == StatusPedido.FINALIZADO);
        verificarIgual("Peso final produzido registrado", 1.55, pedido.getPesoFinalProduzido());

        pedido.marcarEntregue();
        verificar("Loja recebe → ENTREGUE", pedido.getStatus() == StatusPedido.ENTREGUE);

        pedido.cancelar();
        verificar("Cancelado mesmo depois de entregue → CANCELADO", pedido.getStatus() == StatusPedido.CANCELADO);
        verificar("Pedido cancelado continua no sistema", c.sistema.buscarPedido(pedido.getId()) == pedido);

        pedido.registrarImpressaoFabrica();
        verificar("Imprimir pedido cancelado não altera o status", pedido.getStatus() == StatusPedido.CANCELADO);
        verificarLancaExcecao("Não cancela duas vezes", IllegalStateException.class, pedido::cancelar);

        PedidoVenda finalizado = c.pedido(c.caxias, unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18));
        finalizado.registrarImpressaoFabrica();
        finalizado.finalizar(null);
        finalizado.cancelar();
        verificar("Pode cancelar depois de FINALIZADO", finalizado.getStatus() == StatusPedido.CANCELADO);
    }

    // ==================== EDIÇÃO ====================

    private static void testarEdicao() {
        Cenario c = new Cenario();
        PedidoVenda pedido = c.pedido(c.caxias, unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18));

        verificar("Pedido recém-lançado não está *Editado*", !pedido.isEditado());
        verificar("Via fábrica sem *Editado*", !ImpressaoPedido.gerarViaFabrica(pedido).contains("*Editado*"));

        pedido.editarUnidade(1, unidade(MODELO_A, TipoAro.F, 21, TeorOuro.K18));
        verificar("Editar antes de FINALIZADO é permitido", pedido.getUnidades().get(0).getAro() == 21);
        verificar("Edição marca *Editado*", pedido.isEditado());
        verificar("Via fábrica mostra *Editado*", ImpressaoPedido.gerarViaFabrica(pedido).contains("*Editado*"));

        pedido.registrarImpressaoFabrica();
        pedido.editarDadosVenda("Outro vendedor", pedido.getDataVenda(), pedido.getDataEntrega());
        verificar("Editar com status OK é permitido", pedido.getVendedor().equals("Outro vendedor"));

        pedido.finalizar(null);
        verificarLancaExcecao("Editar unidade depois de FINALIZADO é bloqueado", IllegalStateException.class,
                () -> pedido.editarUnidade(1, unidade(MODELO_A, TipoAro.F, 22, TeorOuro.K18)));
        verificarLancaExcecao("Editar dados depois de FINALIZADO é bloqueado", IllegalStateException.class,
                () -> pedido.editarDadosVenda("X", "X", "X"));
        verificarLancaExcecao("Alterar desconto depois de FINALIZADO é bloqueado", IllegalStateException.class,
                () -> pedido.aplicarDesconto(10));
        verificar("Pedido FINALIZADO não pode ser editado", !pedido.podeSerEditado());
    }

    // ==================== FATURAMENTO ====================

    private static void testarFaturamento() {
        Cenario c = new Cenario();

        c.pedidoComValores(c.caxias); // 1615
        c.pedido(c.portoAlegre,
                unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18),
                unidade(MODELO_A, TipoAro.M, 20, TeorOuro.K18)); // 1500
        PedidoVenda cancelado = c.pedido(c.caxias,
                unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18),
                unidade(MODELO_A, TipoAro.M, 20, TeorOuro.K18)); // 1500, será cancelado
        cancelado.cancelar();

        verificarIgual("Caxias: venda normal conta (1615) e cancelada não", 1615, c.sistema.calcularFaturamento(c.caxias));
        verificarIgual("Porto Alegre: 1500", 1500, c.sistema.calcularFaturamento(c.portoAlegre));
        verificarIgual("Novo Hamburgo: 0", 0, c.sistema.calcularFaturamento(c.novoHamburgo));
        verificarIgual("Faturamento total: 1615 + 1500 = 3115", 3115, c.sistema.calcularFaturamentoTotal());
        verificar("Pedido cancelado continua listado", c.sistema.listarPedidos(c.caxias).contains(cancelado));
    }

    // ==================== CLIENTES ====================

    private static void testarClientes() {
        Cenario c = new Cenario();

        verificar("Cliente de Caxias é encontrado em Caxias", c.sistema.buscarPorCpf("111", c.caxias) != null);
        verificar("Cliente de Caxias NÃO é encontrado em Porto Alegre",
                c.sistema.buscarPorCpf("111", c.portoAlegre) == null);
        verificar("Cliente de Caxias NÃO é encontrado em Novo Hamburgo",
                c.sistema.buscarPorCpf("111", c.novoHamburgo) == null);
        verificar("Lista de Caxias só tem clientes de Caxias",
                c.sistema.listarClientes(c.caxias).size() == 1
                        && c.sistema.listarClientes(c.caxias).get(0).pertenceA(c.caxias));
        verificar("ADMIN vê clientes de todas as lojas", c.sistema.listarTodosClientes().size() == 3);

        verificarLancaExcecao("CPF duplicado na mesma loja é rejeitado", IllegalArgumentException.class,
                () -> c.sistema.adicionarCliente(new Cliente("Outro", "111", "", "", "", c.caxias)));

        Cliente clientePoa = c.sistema.buscarPorCpf("222", c.portoAlegre);
        verificarLancaExcecao("Pedido de Caxias não aceita cliente de Porto Alegre", IllegalArgumentException.class,
                () -> c.sistema.novoPedido(c.caxias, clientePoa, "V", "01/01", "10/01"));

        PedidoVenda pedidoCaxias = c.pedido(c.caxias, unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18));
        verificar("Porto Alegre não encontra pedido de Caxias",
                c.sistema.buscarPedido(pedidoCaxias.getId(), c.portoAlegre) == null);
    }

    // ==================== ENTRADA DE DADOS ====================

    private static void testarEntrada() {
        verificar("Aceita vírgula: \"20,5\" → 20.5", Entrada.converterDecimal("20,5") == 20.5);
        verificar("Aceita ponto: \"20.5\" → 20.5", Entrada.converterDecimal("20.5") == 20.5);
        verificar("Texto inválido retorna null (sem exceção)", Entrada.converterDecimal("abc") == null);
        verificar("\"NaN\" é rejeitado", Entrada.converterDecimal("NaN") == null);

        Entrada entrada = new Entrada(new Scanner("abc\n20,5\n"));
        verificarIgual("Entrada inválida pede de novo em vez de quebrar", 20.5, entrada.lerDecimal(""));

        verificarLancaExcecao("Aro zero é rejeitado", IllegalArgumentException.class,
                () -> unidade(MODELO_A, TipoAro.F, 0, TeorOuro.K18));
        verificarLancaExcecao("Teor inválido é rejeitado", IllegalArgumentException.class,
                () -> TeorOuro.deQuilates(14));
        verificarLancaExcecao("Aliança sem modelo é rejeitada", NullPointerException.class,
                () -> new Alianca(null, TipoAro.F, 20, "", TeorOuro.K18, null, false));
    }

    // ==================== IMPRESSÃO ====================

    private static void testarImpressao() {
        Cenario c = new Cenario();
        PedidoVenda pedido = c.pedidoComValores(c.caxias);

        String viaCliente = ImpressaoPedido.gerarViaCliente(pedido);
        verificar("Via cliente mostra CPF", viaCliente.contains("CPF: 111"));
        verificar("Via cliente mostra valor da venda", viaCliente.contains("VALOR DA VENDA"));
        verificar("Via cliente mostra aro e gravação", viaCliente.contains("Aro F") && viaCliente.contains("Gravação"));
        verificar("Via cliente NÃO mostra peso de produção", !viaCliente.contains("Peso de produção"));
        verificar("Via cliente NÃO mostra índices nem percentuais",
                !viaCliente.toLowerCase().contains("índice") && !viaCliente.contains("%"));

        String viaFabrica = ImpressaoPedido.gerarViaFabrica(pedido);
        verificar("Via fábrica mostra peso de produção", viaFabrica.contains("Peso de produção"));
        verificar("Via fábrica NÃO mostra valores em R$", !viaFabrica.contains("R$"));
        verificar("Via fábrica NÃO mostra percentuais", !viaFabrica.contains("%"));
    }

    // ==================== LOGIN ====================

    private static void testarLogin() {
        SistemaShowroom sistema = new SistemaShowroom();
        sistema.adicionarPerfil(new Admin("admin", "admin123"));
        verificar("Login correto retorna o perfil",
                sistema.autenticar("admin", "admin123").orElse(null) instanceof Admin);
        verificar("Senha errada não autentica", sistema.autenticar("admin", "errada").isEmpty());
    }

    // ==================== CENÁRIO DE TESTE ====================

    private static class Cenario {
        final SistemaShowroom sistema = new SistemaShowroom();
        final Loja caxias = new Loja("CDA CAXIAS", "caxias", "x");
        final Loja novoHamburgo = new Loja("CDA NOVO HAMBURGO", "nh", "x");
        final Loja portoAlegre = new Loja("CDA PORTO ALEGRE", "poa", "x");

        Cenario() {
            sistema.adicionarPerfil(caxias);
            sistema.adicionarPerfil(novoHamburgo);
            sistema.adicionarPerfil(portoAlegre);
            sistema.alterarCotacao(300, 500);
            sistema.adicionarCliente(new Cliente("Cliente Caxias", "111", "", "Rua A", "Caxias do Sul", caxias));
            sistema.adicionarCliente(new Cliente("Cliente POA", "222", "", "Rua B", "Porto Alegre", portoAlegre));
            sistema.adicionarCliente(new Cliente("Cliente NH", "333", "", "Rua C", "Novo Hamburgo", novoHamburgo));
        }

        Cliente clienteDa(Loja loja) {
            return sistema.listarClientes(loja).get(0);
        }

        // Cria e registra um pedido com as unidades informadas (varargs).
        PedidoVenda pedido(Loja loja, Alianca... unidades) {
            PedidoVenda pedido = sistema.novoPedido(loja, clienteDa(loja), "Vendedor", "01/09/2026", "30/09/2026");
            for (Alianca unidade : unidades) {
                pedido.adicionarAlianca(unidade);
            }
            sistema.adicionarPedido(pedido);
            return pedido;
        }

        // Par A 18K (base 1500) com +10%, +R$ 50 e 5% de desconto → 1615.
        PedidoVenda pedidoComValores(Loja loja) {
            PedidoVenda pedido = sistema.novoPedido(loja, clienteDa(loja), "Vendedor", "01/09/2026", "30/09/2026");
            pedido.adicionarAlianca(unidade(MODELO_A, TipoAro.F, 20, TeorOuro.K18));
            pedido.adicionarAlianca(unidade(MODELO_A, TipoAro.M, 20, TeorOuro.K18));
            pedido.adicionarAcrescimo(new Acrescimo(10, 0));
            pedido.adicionarAcrescimo(new Acrescimo(0, 50));
            pedido.aplicarDesconto(5);
            sistema.adicionarPedido(pedido);
            return pedido;
        }
    }

    private static Alianca unidade(Modelo modelo, TipoAro tipo, double aro, TeorOuro teor) {
        return new Alianca(modelo, tipo, aro, "Gravação", teor, null, false);
    }

    private static Alianca unidadePE(Modelo modelo, TipoAro tipo, Double larguraPE) {
        return new Alianca(modelo, tipo, 20, "Gravação", TeorOuro.K18, larguraPE, false);
    }

    private static Alianca anatomica(TipoAro tipo, boolean maisAnatomica) {
        return new Alianca(MODELO_A, tipo, 20, "Gravação", TeorOuro.K18, null, maisAnatomica);
    }

    // ==================== VERIFICAÇÕES ====================

    private static void verificar(String descricao, boolean condicao) {
        if (condicao) {
            aprovados++;
            System.out.println("[OK]    " + descricao);
        } else {
            falhas.add(descricao);
            System.out.println("[FALHA] " + descricao);
        }
    }

    private static void verificarIgual(String descricao, double esperado, double obtido) {
        boolean igual = Math.abs(esperado - obtido) < TOLERANCIA;
        verificar(descricao + (igual ? "" : " (esperado " + esperado + ", obtido " + obtido + ")"), igual);
    }

    private static void verificarLancaExcecao(String descricao, Class<? extends Exception> tipoEsperado, Runnable acao) {
        try {
            acao.run();
            verificar(descricao + " (nenhuma exceção lançada)", false);
        } catch (Exception e) {
            verificar(descricao + (tipoEsperado.isInstance(e) ? "" : " (lançou " + e.getClass().getSimpleName() + ")"),
                    tipoEsperado.isInstance(e));
        }
    }

    private static int contar(String texto, String trecho) {
        int quantidade = 0;
        int posicao = texto.indexOf(trecho);
        while (posicao >= 0) {
            quantidade++;
            posicao = texto.indexOf(trecho, posicao + trecho.length());
        }
        return quantidade;
    }
}
