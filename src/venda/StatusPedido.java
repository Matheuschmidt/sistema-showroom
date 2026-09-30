package venda;

// Fluxo: PEDIDO CRIADO → OK (fábrica imprimiu) → FINALIZADO → ENTREGUE.
// Não existe status "EM PRODUÇÃO" (REGRAS_NEGOCIO.md, seção 10).
public enum StatusPedido {
    PEDIDO_CRIADO("PEDIDO CRIADO"),
    OK("OK"),
    FINALIZADO("FINALIZADO"),
    ENTREGUE("ENTREGUE"),
    CANCELADO("CANCELADO");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
