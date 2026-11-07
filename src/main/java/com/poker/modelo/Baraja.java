package com.poker.modelo;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Baraja {
    private List<Carta> cartas;
    private Barajador barajador;

    public Baraja(Barajador barajador) {
        this.barajador = barajador;
        this.cartas = new ArrayList<>();
        reiniciar();
    }

    public void barajar() {
        barajador.barajar(cartas);
    }

    public Carta robar() {
        // Validar que queden cartas
        if (cartas.isEmpty()) {
            throw new IllegalStateException("La baraja no tiene más cartas");
        }
        // Sacar la primera carta
        return cartas.remove(0);
    }

    public void quemar() {
        robar();
    }

    public void reiniciar() {
        // Vaciar la baraja
        cartas.clear();
        // Crear las 52 cartas: 4 palos por 13 valores
        for (Palo palo : Palo.values()) {
            for (Valor valor : Valor.values()) {
                cartas.add(new Carta(valor, palo));
            }
        }
    }
}
