package com.poker.modelo;

import com.poker.dobles.BarajadorOrdenFijo;
import com.poker.dobles.JugadorSimulado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MesaTest {
    private Mesa mesa;
    private Jugador ana;
    private Jugador luis;
    private Jugador sofia;
    private Jugador pedro;

    @BeforeEach
    void setUp() {
        mesa = new Mesa(new Crupier("Crupier", new Baraja(new BarajadorOrdenFijo())));
        ana = new JugadorSimulado("Ana", 100);
        luis = new JugadorSimulado("Luis", 100);
        sofia = new JugadorSimulado("Sofia", 100);
        pedro = new JugadorSimulado("Pedro", 100);
        mesa.agregarJugador(ana);
        mesa.agregarJugador(luis);
        mesa.agregarJugador(sofia);
        mesa.agregarJugador(pedro);
    }

    // La mesa no admite más de 4 jugadores
    @Test
    void agregarJugador() {
        mesa.agregarJugador(new JugadorSimulado("Quinto", 100));
        assertEquals(Mesa.MAX_JUGADORES, mesa.getJugadores().size());
    }

    // Rotar el botón salta a los eliminados
    @Test
    void rotarBoton() {
        luis.setEstado(EstadoJugador.ELIMINADO);
        mesa.rotarBoton();
        assertEquals(2, mesa.getPosicionBoton(), "Debe saltar a Luis porque está eliminado");
    }

    // La ciega pequeña es el jugador a la izquierda del botón
    @Test
    void jugadorCiegaPequena() {
        assertSame(luis, mesa.jugadorCiegaPequena());
    }

    // La ciega grande es el jugador a la izquierda de la pequeña
    @Test
    void jugadorCiegaGrande() {
        assertSame(sofia, mesa.jugadorCiegaGrande());
    }

    // Los retirados y eliminados no siguen en la mano
    @Test
    void jugadoresActivos() {
        luis.retirarse();
        sofia.setEstado(EstadoJugador.ELIMINADO);
        pedro.apostar(100);
        assertEquals(2, mesa.jugadoresActivos().size(), "Solo cuentan Ana (activa) y Pedro (all-in)");
    }
}
