package dominio.produto;

public enum TeorOuro {
    K10(10),
    K18(18);

    private final int quilates;

    TeorOuro(int quilates) {
        this.quilates = quilates;
    }

    public int getQuilates() {
        return quilates;
    }

    public static TeorOuro deQuilates(int quilates) {
        for (TeorOuro teor : values()) {
            if (teor.quilates == quilates) {
                return teor;
            }
        }
        throw new IllegalArgumentException("Teor de ouro inválido: " + quilates + ". Use 10 ou 18.");
    }

    @Override
    public String toString() {
        return quilates + "K";
    }
}
