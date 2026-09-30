package venda;

import dominio.produto.TeorOuro;

// Classe imutável: depois de criada, a cotação nunca muda.
// Quando o ADMIN altera a cotação, o sistema cria um NOVO objeto; os pedidos
// antigos continuam apontando para o objeto antigo (cotação congelada na venda).
public final class CotacaoOuro {
    private final double cotacao10k;
    private final double cotacao18k;

    public CotacaoOuro(double cotacao10k, double cotacao18k) {
        if (cotacao10k <= 0 || cotacao18k <= 0) {
            throw new IllegalArgumentException("A cotação do ouro deve ser maior que zero.");
        }
        this.cotacao10k = cotacao10k;
        this.cotacao18k = cotacao18k;
    }

    public double getCotacao10k() {
        return cotacao10k;
    }

    public double getCotacao18k() {
        return cotacao18k;
    }

    public double getCotacao(TeorOuro teor) {
        return switch (teor) {
            case K10 -> cotacao10k;
            case K18 -> cotacao18k;
        };
    }
}
