package dominio.produto;

public enum TipoAro {
    F,
    M;

    public static TipoAro deTexto(String texto) {
        if (texto != null) {
            String valor = texto.trim().toUpperCase();
            if (valor.equals("F")) {
                return F;
            }
            if (valor.equals("M")) {
                return M;
            }
        }
        throw new IllegalArgumentException("Tipo de aro inválido: " + texto + ". Use F ou M.");
    }
}
