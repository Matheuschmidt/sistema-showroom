package impressao;

import java.math.BigDecimal;
import java.util.Locale;

public class Formatacao {

    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");

    private Formatacao() {
    }

    // 20.0 → "20", 20.5 → "20,5"
    public static String numero(double valor) {
        return BigDecimal.valueOf(valor).stripTrailingZeros().toPlainString().replace('.', ',');
    }

    public static String peso(double valor) {
        return String.format(BRASIL, "%.2f g", valor);
    }

    public static String moeda(double valor) {
        return String.format(BRASIL, "R$ %,.2f", valor);
    }
}
