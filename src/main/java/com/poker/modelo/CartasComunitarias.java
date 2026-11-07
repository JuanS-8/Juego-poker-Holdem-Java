package com.poker.modelo;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CartasComunitarias {
    private List<Carta> cartas;

    public CartasComunitarias() {
        this.cartas = new ArrayList<>();
    }

    public void agregar(Carta carta) {
        // No se agregan cartas null ni más de 5
        if (carta == null || cartas.size() >= 5)
            return;
        cartas.add(carta);
    }

    public void limpiar() {
        cartas.clear();
    }
}
