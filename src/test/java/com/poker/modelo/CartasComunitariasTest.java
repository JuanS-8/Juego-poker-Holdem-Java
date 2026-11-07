package com.poker.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CartasComunitariasTest {
    private CartasComunitarias comunitarias;

    @BeforeEach
    void setUp() {
        comunitarias = new CartasComunitarias();
    }

    // Agregar no pasa de 5 cartas ni acepta null
    @Test
    void agregar() {
        for (Valor valor : Valor.values()) {
            comunitarias.agregar(new Carta(valor, Palo.PICAS));
        }
        comunitarias.agregar(null);
        assertEquals(5, comunitarias.getCartas().size());
    }

    // Limpiar quita todas las cartas
    @Test
    void limpiar() {
        comunitarias.agregar(new Carta(Valor.AS, Palo.PICAS));
        comunitarias.limpiar();
        assertTrue(comunitarias.getCartas().isEmpty());
    }
}
