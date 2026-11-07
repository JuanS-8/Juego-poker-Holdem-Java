package com.poker.servicio;

import com.poker.modelo.EstadoJugador;
import com.poker.modelo.Jugador;

import java.util.*;

public class GestorBote {
    private Map<Jugador, Integer> aportes;

    public GestorBote() {
        this.aportes = new LinkedHashMap<>();
    }

    public void agregarAporte(Jugador jugador, int monto) {
        // Validar los datos
        if (jugador == null || monto <= 0)
            return;
        // Sumar al aporte que ya tenía el jugador
        if (aportes.containsKey(jugador)) {
            aportes.put(jugador, aportes.get(jugador) + monto);
        } else {
            aportes.put(jugador, monto);
        }
    }

    public int total() {
        int total = 0;
        for (int monto : aportes.values()) {
            total = total + monto;
        }
        return total;
    }

    public List<Bote> calcularBotes() {
        List<Bote> botes = new ArrayList<>();

        // Cada aporte distinto de un jugador que sigue en la mano es un nivel de bote
        List<Integer> niveles = new ArrayList<>();
        int mayorAporte = 0;
        for (Jugador jugador : aportes.keySet()) {
            int aporte = aportes.get(jugador);
            mayorAporte = Math.max(mayorAporte, aporte);
            if (jugador.getEstado() != EstadoJugador.RETIRADO && !niveles.contains(aporte)) {
                niveles.add(aporte);
            }
        }
        if (niveles.isEmpty())
            return botes;
        if (!niveles.contains(mayorAporte)) {
            niveles.add(mayorAporte);
        }
        Collections.sort(niveles);

        // Armar un bote por cada nivel, de menor a mayor
        int nivelAnterior = 0;
        for (int nivel : niveles) {
            int monto = 0;
            Set<Jugador> elegibles = new LinkedHashSet<>();
            for (Jugador jugador : aportes.keySet()) {
                int aporte = aportes.get(jugador);
                // Cada jugador pone lo que aportó entre el nivel anterior y este
                monto = monto + Math.min(aporte, nivel) - Math.min(aporte, nivelAnterior);
                // Pueden ganar este bote los que llegaron al nivel y no se retiraron
                if (aporte >= nivel && jugador.getEstado() != EstadoJugador.RETIRADO) {
                    elegibles.add(jugador);
                }
            }
            // Si nadie puede ganarlo, las fichas se suman al bote anterior
            if (elegibles.isEmpty() && !botes.isEmpty()) {
                Bote ultimo = botes.remove(botes.size() - 1);
                botes.add(new Bote(ultimo.monto() + monto, ultimo.elegibles()));
            } else if (monto > 0) {
                botes.add(new Bote(monto, elegibles));
            }
            nivelAnterior = nivel;
        }
        return botes;
    }

    public void reiniciar() {
        aportes.clear();
    }
}
