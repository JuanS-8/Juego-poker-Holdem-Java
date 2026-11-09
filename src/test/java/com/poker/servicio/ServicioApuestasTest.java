package com.poker.servicio;

import com.poker.dobles.BarajadorOrdenFijo;
import com.poker.dobles.EscuchaEventosRegistro;
import com.poker.dobles.JugadorSimulado;
import com.poker.modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServicioApuestasTest {
    private ServicioApuestas servicio;
    private EscuchaEventosRegistro escucha;
    private GestorBote gestor;
    private Mesa mesa;
    private JugadorSimulado boton;
    private JugadorSimulado pequena;
    private JugadorSimulado grande;

    @BeforeEach
    void setUp() {
        escucha = new EscuchaEventosRegistro();
        servicio = new ServicioApuestas(new ValidadorAccion(), escucha);
        gestor = new GestorBote();
        mesa = new Mesa(new Crupier("Crupier", new Baraja(new BarajadorOrdenFijo())));
        boton = new JugadorSimulado("Boton", 1000);
        pequena = new JugadorSimulado("Pequena", 1000, new Accion(TipoAccion.RETIRARSE, 0));
        grande = new JugadorSimulado("Grande", 1000);
        mesa.agregarJugador(boton);
        mesa.agregarJugador(pequena);
        mesa.agregarJugador(grande);
    }

    // Cobrar las ciegas pequeña y grande
    @Test
    void cobrarCiegas() {
        servicio.cobrarCiegas(mesa, 10, 20, gestor);
        assertEquals(990, pequena.getFichas());
        assertEquals(980, grande.getFichas());
        assertEquals(30, gestor.total());
    }

    // Ronda pre-flop: el botón iguala, la pequeña se retira y la grande pasa
    @Test
    void ejecutarRonda() {
        servicio.cobrarCiegas(mesa, 10, 20, gestor);
        assertTrue(servicio.ejecutarRonda(mesa, FaseJuego.PRE_FLOP, gestor), "Siguen 2 jugadores en la mano");
        assertEquals(50, gestor.total());
        assertEquals(EstadoJugador.RETIRADO, pequena.getEstado());
        assertEquals(3, escucha.acciones.size(), "La vista recibe cada acción");
    }

    // El registro guarda las acciones en orden
    @Test
    void obtenerRegistro() {
        servicio.cobrarCiegas(mesa, 10, 20, gestor);
        servicio.ejecutarRonda(mesa, FaseJuego.PRE_FLOP, gestor);
        assertEquals("Boton", servicio.obtenerRegistro().get(0).idJugador(), "Primero habla el de la izquierda de la grande");
        assertEquals("RETIRARSE", servicio.obtenerRegistro().get(1).tipo());
    }

    // Limpiar el registro
    @Test
    void limpiarRegistro() {
        servicio.cobrarCiegas(mesa, 10, 20, gestor);
        servicio.ejecutarRonda(mesa, FaseJuego.PRE_FLOP, gestor);
        servicio.limpiarRegistro();
        assertTrue(servicio.obtenerRegistro().isEmpty());
    }
}
