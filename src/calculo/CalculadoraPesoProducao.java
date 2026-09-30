package calculo;

import dominio.produto.Alianca;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Cálculo do peso de PRODUÇÃO de uma unidade (REGRAS_NEGOCIO.md, seção 6.2).
// Não confundir com o peso comercial, que é usado no valor da venda.
public class CalculadoraPesoProducao {

    private static final BigDecimal MULTIPLO_ARREDONDAMENTO = new BigDecimal("0.05");
    private static final IndiceAro INDICE_ARO = new IndiceAro();

    private CalculadoraPesoProducao() {
    }

    public static double calcularPesoUnidade(Alianca alianca, double percentualAlteracaoPeso) {
        // 1. Peso base da unidade (o sistema já guarda o peso individual: não dividir por 2).
        double peso = alianca.getModelo().getPesoBase(alianca.getTeorOuro());

        // 2. Alteração percentual de peso da venda, aplicada no início.
        peso = peso * (1 + percentualAlteracaoPeso / 100);

        // 3. P.E.: peso ÷ largura original × largura desejada.
        //    Equivale à regra do par: (par ÷ largura × desejada) ÷ 2 = (par ÷ 2) ÷ largura × desejada.
        if (alianca.possuiPE()) {
            peso = peso / alianca.getModelo().getLargura() * alianca.getLarguraPE();
        }

        // 4. Índice do aro desta unidade.
        peso = peso * INDICE_ARO.buscarIndice(alianca.getAro());

        // 5. Arredondamento individual para cima em múltiplos de 0,05 g.
        return arredondarParaCima(peso);
    }

    // Equivale a Math.ceil(peso / 0.05) * 0.05, mas com BigDecimal para evitar
    // erros de ponto flutuante do double. Ex.: 1.50 × 0.8 (aro 8) = 1.2000000000000002,
    // que com Math.ceil puro subiria indevidamente para 1.25 (o correto é 1.20).
    public static double arredondarParaCima(double peso) {
        BigDecimal valor = BigDecimal.valueOf(peso).setScale(6, RoundingMode.HALF_UP);
        BigDecimal quantidadeDeMultiplos = valor.divide(MULTIPLO_ARREDONDAMENTO, 0, RoundingMode.CEILING);
        return quantidadeDeMultiplos.multiply(MULTIPLO_ARREDONDAMENTO).doubleValue();
    }
}
