package catalogo;

import dominio.produto.Modelo;

import java.util.HashMap;
import java.util.Map;

public class CatalogoModelos {

    private Map<String, Modelo> modelos = new HashMap<>();

    public CatalogoModelos() {

        // CDA02 - CDA08
        modelos.put("CDA02", new Modelo("CDA02", 2, 20, 0.60, 0.75));
        modelos.put("CDA03", new Modelo("CDA03", 3, 20, 0.85, 1.15));
        modelos.put("CDA04", new Modelo("CDA04", 4, 20, 1.15, 1.50));
        modelos.put("CDA05", new Modelo("CDA05", 5, 20, 1.60, 1.95));
        modelos.put("CDA06", new Modelo("CDA06", 6, 20, 1.95, 2.60));
        modelos.put("CDA07", new Modelo("CDA07", 7, 20, 2.80, 3.35));
        modelos.put("CDA08", new Modelo("CDA08", 8, 20, 3.30, 3.75));


        // CDA02A - CDA08A
        modelos.put("CDA02A", new Modelo("CDA02A", 2, 20, 0.80, 0.95));
        modelos.put("CDA03A", new Modelo("CDA03A", 3, 20, 1.05, 1.45));
        modelos.put("CDA04A", new Modelo("CDA04A", 4, 20, 1.40, 1.80));
        modelos.put("CDA05A", new Modelo("CDA05A", 5, 20, 2.10, 2.40));
        modelos.put("CDA06A", new Modelo("CDA06A", 6, 20, 2.75, 3.50));
        modelos.put("CDA07A", new Modelo("CDA07A", 7, 20, 3.75, 4.50));
        modelos.put("CDA08A", new Modelo("CDA08A", 8, 20, 4.25, 5.25));


        // CDA02MC - CDA08MC
        modelos.put("CDA02MC", new Modelo("CDA02MC", 2, 20, 0.80, 0.95));
        modelos.put("CDA03MC", new Modelo("CDA03MC", 3, 20, 1.05, 1.45));
        modelos.put("CDA04MC", new Modelo("CDA04MC", 4, 20, 1.50, 1.90));
        modelos.put("CDA05MC", new Modelo("CDA05MC", 5, 20, 2.10, 2.40));
        modelos.put("CDA06MC", new Modelo("CDA06MC", 6, 20, 2.90, 3.80));
        modelos.put("CDA07MC", new Modelo("CDA07MC", 7, 20, 3.75, 4.50));
        modelos.put("CDA08MC", new Modelo("CDA08MC", 8, 20, 4.25, 5.25));


        // CDA02MCA - CDA08MCA
        modelos.put("CDA02MCA", new Modelo("CDA02MCA", 2, 20, 0.90, 1.125));
        modelos.put("CDA03MCA", new Modelo("CDA03MCA", 3, 20, 1.275, 1.725));
        modelos.put("CDA04MCA", new Modelo("CDA04MCA", 4, 20, 1.725, 2.25));
        modelos.put("CDA05MCA", new Modelo("CDA05MCA", 5, 20, 2.40, 2.925));
        modelos.put("CDA06MCA", new Modelo("CDA06MCA", 6, 20, 2.925, 3.90));
        modelos.put("CDA07MCA", new Modelo("CDA07MCA", 7, 20, 4.20, 5.025));
        modelos.put("CDA08MCA", new Modelo("CDA08MCA", 8, 20, 4.40, 5.625));
    }

    public Modelo buscarModelo(String referencia) {
        return modelos.get(referencia);
    }
}