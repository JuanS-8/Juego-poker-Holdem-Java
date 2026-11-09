package com.poker.modelo;

import com.poker.bot.EstrategiaAleatoria;
import com.poker.dobles.JugadorSimulado;
import com.poker.dobles.ProveedorAccionSimulado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class JugadorTest {
    private Jugador jugador;

    @BeforeEach
    void setUp() {
        jugador = new JugadorSimulado("Ana", 100);
    }

    // Metodo para no repetir el contexto
    private ContextoDecision nuevoContexto() {
        return new ContextoDecision(List.of(), List.of(), 0, 20, 0, 100, FaseJuego.PRE_FLOP, Set.of(TipoAccion.PASAR));
    }

    // Apostar resta fichas y si apuesta todo queda all-in
    @Test
    void apostar() {
        assertEquals(30, jugador.apostar(30));
        assertEquals(70, jugador.getFichas());
        assertEquals(70, jugador.apostar(500), "No puede apostar más fichas de las que tiene");
        assertEquals(EstadoJugador.ALL_IN, jugador.getEstado());
    }

    // Retirarse cambia el estado
    @Test
    void retirarse() {
        jugador.retirarse();
        assertEquals(EstadoJugador.RETIRADO, jugador.getEstado());
    }

    // Recibe máximo dos cartas
    @Test
    void recibirCarta() {
        jugador.recibirCarta(new Carta(Valor.AS, Palo.PICAS));
        jugador.recibirCarta(new Carta(Valor.REY, Palo.PICAS));
        jugador.recibirCarta(new Carta(Valor.REINA, Palo.PICAS));
        assertEquals(2, jugador.getCartas().size());
    }

    // Limpiar la mano y quedar eliminado si no tiene fichas
    @Test
    void limpiarMano() {
        jugador.recibirCarta(new Carta(Valor.AS, Palo.PICAS));
        jugador.apostar(100);
        jugador.limpiarMano();
        assertTrue(jugador.getCartas().isEmpty());
        assertEquals(0, jugador.getApuestaActual());
        assertEquals(EstadoJugador.ELIMINADO, jugador.getEstado());
    }

    // El humano le pide la acción al proveedor
    @Test
    void decidirAccionHumano() {
        JugadorHumano humano = new JugadorHumano("1", "Juan", 100, new ProveedorAccionSimulado());
        assertEquals(TipoAccion.PASAR, humano.decidirAccion(nuevoContexto()).tipo());
    }

    // El bot le pide la acción a su estrategia
    @Test
    void decidirAccionBot() {
        JugadorBot bot = new JugadorBot("2", "Bot", 100, new EstrategiaAleatoria(new Random()));
        assertEquals(TipoAccion.PASAR, bot.decidirAccion(nuevoContexto()).tipo());
    }
}
