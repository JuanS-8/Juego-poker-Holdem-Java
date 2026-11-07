package com.poker.modelo;

import com.poker.dobles.BarajadorOrdenFijo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class BarajaTest {
    private Baraja baraja;

    @BeforeEach
    void setUp() {
        baraja = new Baraja(new BarajadorOrdenFijo());
    }

    // Barajar con orden fijo deja las cartas indicadas primero
    @Test
    void barajar() {
        Carta primera = new Carta(Valor.AS, Palo.CORAZONES);
        Baraja ordenada = new Baraja(new BarajadorOrdenFijo(List.of(primera)));
        ordenada.barajar();
        assertEquals(primera, ordenada.robar());
    }

    // Robar quita una carta y con la baraja vacía lanza excepción
    @Test
    void robar() {
        Carta carta = baraja.robar();
        assertEquals(51, baraja.getCartas().size());
        assertFalse(baraja.getCartas().contains(carta), "La carta robada ya no debe estar en la baraja");
        for (int i = 0; i < 51; i++) {
            baraja.robar();
        }
        assertThrows(IllegalStateException.class, () -> baraja.robar());
    }

    // Quemar quita una carta
    @Test
    void quemar() {
        baraja.quemar();
        assertEquals(51, baraja.getCartas().size());
    }

    // Reiniciar deja 52 cartas distintas
    @Test
    void reiniciar() {
        baraja.robar();
        baraja.reiniciar();
        assertEquals(52, baraja.getCartas().size());
        assertEquals(52, new HashSet<>(baraja.getCartas()).size(), "No debe haber cartas repetidas");
    }

    // El barajador aleatorio mezcla sin perder cartas
    @Test
    void barajadorAleatorio() {
        Baraja aleatoria = new Baraja(new BarajadorAleatorio(new Random(7)));
        aleatoria.barajar();
        assertEquals(52, new HashSet<>(aleatoria.getCartas()).size());
    }
}
