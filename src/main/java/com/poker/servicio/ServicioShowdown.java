package com.poker.servicio;

import com.poker.evaluador.EvaluadorMano;
import com.poker.evaluador.ResultadoMano;
import com.poker.modelo.CartasComunitarias;
import com.poker.modelo.Jugador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServicioShowdown {
    private EvaluadorMano evaluador;

    public ServicioShowdown(EvaluadorMano evaluador) {
        this.evaluador = evaluador;
    }

    public Map<Jugador, Integer> repartirBotes(List<Bote> botes, CartasComunitarias comunitarias) {
        Map<Jugador, Integer> ganancias = new LinkedHashMap<>();
        if (botes == null)
            return ganancias;

        for (Bote bote : botes) {
            // Buscar los ganadores del bote
            List<Jugador> ganadores = new ArrayList<>();
            if (bote.elegibles().size() == 1) {
                // Un solo jugador se lleva el bote sin comparar
                ganadores.addAll(bote.elegibles());
            } else {
                // Comparar la mano de cada jugador y guardar los que tengan la mejor
                ResultadoMano mejor = null;
                for (Jugador jugador : bote.elegibles()) {
                    ResultadoMano resultado = evaluador.evaluar(jugador.getCartas(), comunitarias.getCartas());
                    if (resultado == null)
                        continue;
                    if (mejor == null || resultado.compareTo(mejor) > 0) {
                        mejor = resultado;
                        ganadores.clear();
                        ganadores.add(jugador);
                    } else if (resultado.compareTo(mejor) == 0) {
                        ganadores.add(jugador);
                    }
                }
            }
            // Si no se pudo evaluar ninguna mano, todos los elegibles empatan
            if (ganadores.isEmpty()) {
                ganadores.addAll(bote.elegibles());
            }

            // Repartir el bote en partes iguales
            int parte = bote.monto() / ganadores.size();
            int sobrante = bote.monto() % ganadores.size();
            for (Jugador ganador : ganadores) {
                int anterior = ganancias.containsKey(ganador) ? ganancias.get(ganador) : 0;
                ganancias.put(ganador, anterior + parte);
            }
            // Las fichas que sobran van al primer ganador desde la ciega pequeña
            Jugador primero = ganadores.get(0);
            ganancias.put(primero, ganancias.get(primero) + sobrante);
        }
        return ganancias;
    }
}
