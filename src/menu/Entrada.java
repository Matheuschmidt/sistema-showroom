package menu;

import dominio.produto.TeorOuro;
import dominio.produto.TipoAro;
import impressao.Formatacao;

import java.util.Scanner;

// Leitura de dados do console com validação.
// Entradas inválidas pedem o valor de novo em vez de derrubar o programa.
public class Entrada {

    private final Scanner scanner;

    public Entrada(Scanner scanner) {
        this.scanner = scanner;
    }

    public String lerTexto(String rotulo) {
        System.out.print(rotulo);
        return scanner.nextLine().trim();
    }

    public String lerTextoObrigatorio(String rotulo) {
        while (true) {
            String texto = lerTexto(rotulo);
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("Campo obrigatório.");
        }
    }

    // Enter mantém o valor atual (usado na edição).
    public String lerTextoComPadrao(String rotulo, String atual) {
        String texto = lerTexto(rotulo + " [" + atual + "]: ");
        return texto.isEmpty() ? atual : texto;
    }

    public int lerInteiro(String rotulo) {
        while (true) {
            String texto = lerTexto(rotulo);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Número inválido.");
            }
        }
    }

    public double lerDecimal(String rotulo) {
        while (true) {
            Double valor = converterDecimal(lerTexto(rotulo));
            if (valor != null) {
                return valor;
            }
            System.out.println("Número inválido. Exemplo: 20,5");
        }
    }

    public double lerDecimalPositivo(String rotulo) {
        while (true) {
            double valor = lerDecimal(rotulo);
            if (valor > 0) {
                return valor;
            }
            System.out.println("Informe um valor maior que zero.");
        }
    }

    public double lerDecimalNaoNegativo(String rotulo) {
        while (true) {
            double valor = lerDecimal(rotulo);
            if (valor >= 0) {
                return valor;
            }
            System.out.println("Informe um valor maior ou igual a zero.");
        }
    }

    // Enter = sem valor (retorna null).
    public Double lerDecimalPositivoOpcional(String rotulo) {
        while (true) {
            String texto = lerTexto(rotulo);
            if (texto.isEmpty()) {
                return null;
            }
            Double valor = converterDecimal(texto);
            if (valor != null && valor > 0) {
                return valor;
            }
            System.out.println("Número inválido. Deixe em branco se não houver.");
        }
    }

    public double lerDecimalComPadrao(String rotulo, double atual) {
        while (true) {
            String texto = lerTexto(rotulo + " [" + Formatacao.numero(atual) + "]: ");
            if (texto.isEmpty()) {
                return atual;
            }
            Double valor = converterDecimal(texto);
            if (valor != null) {
                return valor;
            }
            System.out.println("Número inválido. Exemplo: 20,5");
        }
    }

    // P.E. na edição: Enter mantém, 0 remove.
    public Double lerPEComPadrao(String rotulo, Double atual) {
        String descricaoAtual = atual == null ? "sem P.E." : Formatacao.numero(atual) + " mm";
        while (true) {
            String texto = lerTexto(rotulo + " [" + descricaoAtual + " | Enter mantém | 0 remove]: ");
            if (texto.isEmpty()) {
                return atual;
            }
            Double valor = converterDecimal(texto);
            if (valor != null && valor == 0) {
                return null;
            }
            if (valor != null && valor > 0) {
                return valor;
            }
            System.out.println("Número inválido.");
        }
    }

    public boolean lerSimNao(String rotulo) {
        while (true) {
            String texto = lerTexto(rotulo).toUpperCase();
            if (texto.equals("S")) {
                return true;
            }
            if (texto.equals("N")) {
                return false;
            }
            System.out.println("Responda S ou N.");
        }
    }

    public boolean lerSimNaoComPadrao(String rotulo, boolean atual) {
        while (true) {
            String texto = lerTexto(rotulo + " [" + (atual ? "S" : "N") + "]: ").toUpperCase();
            if (texto.isEmpty()) {
                return atual;
            }
            if (texto.equals("S")) {
                return true;
            }
            if (texto.equals("N")) {
                return false;
            }
            System.out.println("Responda S ou N.");
        }
    }

    public TeorOuro lerTeor(String rotulo) {
        while (true) {
            try {
                return TeorOuro.deQuilates(lerInteiro(rotulo));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public TeorOuro lerTeorComPadrao(String rotulo, TeorOuro atual) {
        while (true) {
            String texto = lerTexto(rotulo + " [" + atual.getQuilates() + "]: ");
            if (texto.isEmpty()) {
                return atual;
            }
            try {
                return TeorOuro.deQuilates(Integer.parseInt(texto));
            } catch (IllegalArgumentException e) {
                System.out.println("Teor inválido. Use 10 ou 18.");
            }
        }
    }

    public TipoAro lerTipoAro(String rotulo) {
        while (true) {
            try {
                return TipoAro.deTexto(lerTexto(rotulo));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public TipoAro lerTipoAroComPadrao(String rotulo, TipoAro atual) {
        while (true) {
            String texto = lerTexto(rotulo + " [" + atual + "]: ");
            if (texto.isEmpty()) {
                return atual;
            }
            try {
                return TipoAro.deTexto(texto);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Aceita vírgula ou ponto como separador decimal. Retorna null se inválido.
    public static Double converterDecimal(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            double valor = Double.parseDouble(texto.trim().replace(',', '.'));
            return Double.isFinite(valor) ? valor : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
