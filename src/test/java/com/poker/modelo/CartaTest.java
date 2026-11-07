package com.poker.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CartaTest {

    // El texto de la carta tiene su valor y su palo
    @Test
    void textoDeLaCarta() {
        assertEquals("REY de TREBOLES", new Carta(Valor.REY, Palo.TREBOLES).toString());
    }
}
