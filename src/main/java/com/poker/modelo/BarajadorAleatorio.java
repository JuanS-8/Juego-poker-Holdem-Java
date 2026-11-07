package com.poker.modelo;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BarajadorAleatorio implements Barajador {
    private Random aleatorio;

    public BarajadorAleatorio() {
        this.aleatorio = new Random();
    }

    public BarajadorAleatorio(Random aleatorio) {
        this.aleatorio = aleatorio;
    }

    @Override
    public void barajar(List<Carta> cartas) {
        if (cartas == null)
            return;
        Collections.shuffle(cartas, aleatorio);
    }
}
