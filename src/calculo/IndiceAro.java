package calculo;

import java.util.HashMap;
import java.util.Map;

public class IndiceAro {

    private Map<Integer, Double> indices = new HashMap<>();
    public IndiceAro() {

        indices.put(8, 0.8000);
        indices.put(9, 0.8166);
        indices.put(10, 0.8333);
        indices.put(11, 0.8500);
        indices.put(12, 0.8666);
        indices.put(13, 0.8833);
        indices.put(14, 0.9000);
        indices.put(15, 0.9166);
        indices.put(16, 0.9333);
        indices.put(17, 0.9500);
        indices.put(18, 0.9666);
        indices.put(19, 0.9833);
        indices.put(20, 1.0000);
        indices.put(21, 1.0166);
        indices.put(22, 1.0330);
        indices.put(23, 1.0500);
        indices.put(24, 1.0666);
        indices.put(25, 1.0833);
        indices.put(26, 1.1000);
        indices.put(27, 1.1166);
        indices.put(28, 1.1333);
        indices.put(29, 1.1500);
        indices.put(30, 1.1666);
        indices.put(31, 1.1833);
        indices.put(32, 1.2000);
        indices.put(33, 1.2166);
        indices.put(34, 1.2330);
        indices.put(35, 1.2500);
        indices.put(36, 1.2666);
        indices.put(37, 1.2833);
        indices.put(38, 1.3000);
        indices.put(39, 1.3166);
        indices.put(40, 1.3330);
    }

    public double buscarIndice(int aro) {
        return indices.get(aro);
    }
}
