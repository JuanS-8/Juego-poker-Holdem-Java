package com.poker.modelo;

import lombok.Getter;

@Getter
public enum Valor {
    DOS(2), TRES(3), CUATRO(4), CINCO(5), SEIS(6), SIETE(7), OCHO(8), NUEVE(9), DIEZ(10), JOTA(11), REINA(12), REY(13),
            AS(14);

    private final int numero;

    Valor(int numero) {
        this.numero = numero;
    }
}
