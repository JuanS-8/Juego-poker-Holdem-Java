package com.poker.evaluador.reglas;

import com.poker.evaluador.ResultadoMano;
import com.poker.modelo.Carta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface ReglaMano {

    Optional<ResultadoMano> verificar(List<Carta> cincoCartas);

    static boolean sonCincoCartas(List<Carta> cartas) {
        return cartas != null && cartas.size() == 5;
    }

    static List<Integer> cantidades(List<Carta> cartas) {
        // Cuántas veces aparece cada valor
        List<Integer> cantidades = new ArrayList<>();
        for (int valor : valoresDistintos(cartas)) {
            cantidades.add(contarValor(cartas, valor));
        }
        // Ordenar de mayor a menor
        Collections.sort(cantidades);
        Collections.reverse(cantidades);
        return cantidades;
    }

    static List<Integer> desempatePorGrupos(List<Carta> cartas) {
        // Valores distintos de mayor a menor
        List<Integer> valores = valoresDistintos(cartas);
        Collections.sort(valores);
        Collections.reverse(valores);
        // Primero los grupos más grandes
        List<Integer> desempate = new ArrayList<>();
        for (int cantidad = 4; cantidad >= 1; cantidad--) {
            for (int valor : valores) {
                if (contarValor(cartas, valor) == cantidad) {
                    desempate.add(valor);
                }
            }
        }
        return desempate;
    }

    static boolean esColor(List<Carta> cartas) {
        // Todas las cartas deben tener el palo de la primera
        for (Carta carta : cartas) {
            if (carta.palo() != cartas.get(0).palo())
                return false;
        }
        return true;
    }

    static int cartaAltaEscalera(List<Carta> cartas) {
        // Una escalera tiene 5 valores distintos
        List<Integer> valores = valoresDistintos(cartas);
        if (valores.size() != 5)
            return 0;
        Collections.sort(valores);
        // El As cuenta como 1 en la escalera A-2-3-4-5
        if (valores.get(0) == 2 && valores.get(1) == 3 && valores.get(2) == 4 && valores.get(3) == 5
                && valores.get(4) == 14)
            return 5;
        // Valores seguidos: de la menor a la mayor hay 4 de diferencia
        if (valores.get(4) - valores.get(0) == 4)
            return valores.get(4);
        return 0;
    }

    private static List<Integer> valoresDistintos(List<Carta> cartas) {
        List<Integer> valores = new ArrayList<>();
        for (Carta carta : cartas) {
            if (!valores.contains(carta.valor().getNumero())) {
                valores.add(carta.valor().getNumero());
            }
        }
        return valores;
    }

    private static int contarValor(List<Carta> cartas, int valor) {
        int cantidad = 0;
        for (Carta carta : cartas) {
            if (carta.valor().getNumero() == valor) {
                cantidad++;
            }
        }
        return cantidad;
    }
}
