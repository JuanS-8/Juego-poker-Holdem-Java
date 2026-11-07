package com.poker.modelo;

import com.poker.dobles.JugadorSimulado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JugadorTest {
    private Jugador jugador;

    @BeforeEach
    void setUp() {
        jugador = new JugadorSimulado("Ana", 100);
    }

    // Apostar resta fichas y si apuesta todo queda all-in
    @Test
    void apostar() {
        assertEquals(30, jugador.apostar(30));
        assertEquals(70, jugador.getFichas());
        assertEquals(70, jugador.apostar(500), "No puede apostar más fichas de las que tiene");
        assertEquals(EstadoJugador.ALL_IN, jugador.getEstado());
    }
}
