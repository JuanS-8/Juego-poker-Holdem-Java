package com.poker.servicio;

import com.poker.dobles.JugadorSimulado;
import com.poker.excepcion.ExcepcionAccionInvalida;
import com.poker.modelo.Accion;
import com.poker.modelo.Jugador;
import com.poker.modelo.TipoAccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ValidadorAccionTest {
    private ValidadorAccion validador;
    private Jugador jugador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorAccion();
        jugador = new JugadorSimulado("Ana", 100);
    }

    // Con apuesta pendiente no se puede pasar
    @Test
    void accionesValidas() {
        Set<TipoAccion> validas = validador.accionesValidas(jugador, 40, 20);
        assertFalse(validas.contains(TipoAccion.PASAR), "Solo se puede pasar si no hay apuesta pendiente");
        assertTrue(validas.contains(TipoAccion.IGUALAR));
        assertTrue(validas.contains(TipoAccion.SUBIR));
    }

    // Una subida menor a la mínima es inválida
    @Test
    void validar() {
        ExcepcionAccionInvalida error = assertThrows(ExcepcionAccionInvalida.class,
                () -> validador.validar(new Accion(TipoAccion.SUBIR, 50), jugador, 40, 20));
        assertTrue(error.getMessage().contains("60"), "El mensaje debe indicar el mínimo de 60");
    }
}
