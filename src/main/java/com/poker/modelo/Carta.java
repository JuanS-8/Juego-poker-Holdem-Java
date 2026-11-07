package com.poker.modelo;

import java.io.Serializable;

public record Carta(Valor valor, Palo palo) implements Serializable {

    @Override
    public String toString() {
        return valor + " de " + palo;
    }
}
