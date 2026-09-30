package impressao;

import dominio.produto.Alianca;
import venda.PedidoVenda;

// Monta o texto das vias do pedido (REGRAS_NEGOCIO.md, seção 9).
// - Via do cliente: dados comerciais + descrição do produto + valor da venda.
// - Via da fábrica: descrição do produto + informações de produção (pesos).
// Nenhuma via mostra fórmulas, índices de aro ou percentuais internos.
public class ImpressaoPedido {

    private static final String LINHA = "-------------------------------------";

    private ImpressaoPedido() {
    }

    public static String gerarViaCliente(PedidoVenda pedido) {
        StringBuilder texto = new StringBuilder();

        texto.append("========== PEDIDO DE VENDA ==========\n");
        texto.append("Pedido Nº: ").append(pedido.getId()).append('\n');
        texto.append("Loja: ").append(pedido.getLoja().getNome()).append('\n');
        texto.append("Vendedor: ").append(pedido.getVendedor()).append('\n');
        texto.append("Cliente: ").append(pedido.getCliente().getNome()).append('\n');
        texto.append("CPF: ").append(pedido.getCliente().getCpf()).append('\n');
        texto.append("Endereço: ").append(pedido.getCliente().getEndereco())
                .append(" - ").append(pedido.getCliente().getCidade()).append('\n');
        texto.append("Data da venda: ").append(pedido.getDataVenda()).append('\n');
        texto.append("Data prometida: ").append(pedido.getDataEntrega()).append('\n');
        texto.append(LINHA).append('\n');

        adicionarDescricaoProduto(texto, pedido, false);

        texto.append(LINHA).append('\n');
        texto.append("VALOR DA VENDA: ").append(Formatacao.moeda(pedido.calcularValorFinal())).append('\n');
        texto.append("=====================================\n");

        return texto.toString();
    }

    public static String gerarViaFabrica(PedidoVenda pedido) {
        StringBuilder texto = new StringBuilder();

        texto.append("========== PEDIDO FÁBRICA ==========\n");
        if (pedido.isEditado()) {
            texto.append("*Editado*\n");
        }
        texto.append("Pedido Nº: ").append(pedido.getId()).append('\n');
        texto.append("Loja: ").append(pedido.getLoja().getNome()).append('\n');
        texto.append("Status: ").append(pedido.getStatus()).append('\n');
        texto.append("Data prometida: ").append(pedido.getDataEntrega()).append('\n');
        texto.append(LINHA).append('\n');

        adicionarDescricaoProduto(texto, pedido, true);

        texto.append(LINHA).append('\n');
        texto.append("Peso estimado de produção: ")
                .append(Formatacao.peso(pedido.calcularPesoProducaoTotal())).append('\n');
        if (pedido.getPesoFinalProduzido() != null) {
            texto.append("Peso final produzido: ")
                    .append(Formatacao.peso(pedido.getPesoFinalProduzido())).append('\n');
        }
        texto.append("====================================\n");

        return texto.toString();
    }

    // Área única de P.E., com a largura de cada unidade. Ex.: "F 4,5 mm / M 5,5 mm".
    // Retorna null quando nenhuma unidade tem P.E.
    public static String descreverPE(PedidoVenda pedido) {
        StringBuilder texto = new StringBuilder();
        for (Alianca unidade : pedido.getUnidades()) {
            if (unidade.possuiPE()) {
                if (texto.length() > 0) {
                    texto.append(" / ");
                }
                texto.append(unidade.getTipoAro()).append(' ')
                        .append(Formatacao.numero(unidade.getLarguraPE())).append(" mm");
            }
        }
        return texto.length() == 0 ? null : texto.toString();
    }

    private static void adicionarDescricaoProduto(StringBuilder texto, PedidoVenda pedido, boolean viaFabrica) {
        int numero = 1;
        for (Alianca unidade : pedido.getUnidades()) {
            texto.append("Unidade ").append(numero).append(": ")
                    .append(unidade.getModelo().getReferencia()).append(' ')
                    .append(unidade.getTeorOuro()).append('\n');

            if (unidade.isMaisAnatomica()) {
                texto.append("  MAIS ANATÔMICA\n");
            }

            texto.append("  Aro ").append(unidade.getTipoAro()).append(": ")
                    .append(Formatacao.numero(unidade.getAro()))
                    .append(" - Gravação: ").append(unidade.getGravacao()).append('\n');

            if (viaFabrica) {
                texto.append("  Peso de produção: ")
                        .append(Formatacao.peso(pedido.calcularPesoProducao(unidade)));
                if (unidade.isMaisAnatomica()) {
                    texto.append(" (sem ajuste de mais anatômica: percentual ainda não definido)");
                }
                texto.append('\n');
            }
            numero++;
        }

        String pe = descreverPE(pedido);
        if (pe != null) {
            texto.append("P.E.: ").append(pe).append('\n');
        }
    }
}
