package com.poker.evaluador;

import com.poker.modelo.Carta;

import java.util.List;

public record ResultadoMano(CategoriaMano categoria, List<Carta> mejoresCinco,
        List<Integer> desempate) implements Comparable<ResultadoMano> {

    @Override
    public int compareTo(ResultadoMano otro) {
        // Gana la categoría más alta
        int comparacion = categoria.compareTo(otro.categoria());
        if (comparacion != 0)
            return comparacion;
        // Misma categoría: se comparan los valores de desempate en orden
        int cantidad = Math.min(desempate.size(), otro.desempate().size());
        for (int i = 0; i < cantidad; i++) {
            comparacion = Integer.compare(desempate.get(i), otro.desempate().get(i));
            if (comparacion != 0)
                return comparacion;
        }
        // Si todo es igual hay empate
        return 0;
    }
}
