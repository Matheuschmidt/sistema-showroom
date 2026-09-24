public class Main {
    public static void main(String[] args) {
        Loja loja = new Loja("CDA CAXIAS", "cda caxias", "123");
        Cliente cliente = new Cliente("Matheus", "600.878.920-22", "51 992819790", "Porto Alegra");
        Modelo modelo = new Modelo("CDA05MCA", 5, 20, 2, 2.6);
        Modelo modelo1 = new Modelo("CDA04MCA", 4, 20, 1.75,2.1);
        Alianca alianca = new Alianca(modelo, "F", 15, "EU TE AMO para sempre Matheus <3", 1.15, 10);
        Alianca alianca1 = new Alianca(modelo, "M", 20, "EU TE AMO para sempre Thayna <3", 1.75, 10);
        Venda venda = new Venda(cliente, loja, alianca1, alianca, "Marcelo", "23/09/2026", "26/0/2026", 10);

        venda.exibirVenda();
    }

}
