package com.poker.dobles;

import com.poker.modelo.Barajador;
import com.poker.modelo.Carta;

import java.util.ArrayList;
import java.util.List;

public class BarajadorOrdenFijo implements Barajador {
    private final List<Carta> orden;

    public BarajadorOrdenFijo() {
        this.orden = new ArrayList<>();
    }

    public BarajadorOrdenFijo(List<Carta> orden) {
        this.orden = new ArrayList<>(orden);
    }

    @Override
    public void barajar(List<Carta> cartas) {
        // Quitar las cartas indicadas y ponerlas al principio
        cartas.removeAll(orden);
        cartas.addAll(0, orden);
    }
}
