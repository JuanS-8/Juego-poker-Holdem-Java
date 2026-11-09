package com.poker.servicio;

import com.poker.bot.DificultadBot;
import com.poker.dobles.*;
import com.poker.dto.HistorialManoDTO;
import com.poker.dto.JugadorDTO;
import com.poker.evaluador.EvaluadorManoEstandar;
import com.poker.modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ServicioJuegoTest {
    private ServicioJuego servicio;
    private Mesa mesa;
    private JugadorDAOMemoria jugadorDAO;
    private PartidaDAOMemoria partidaDAO;
    private HistorialManoDAOMemoria historialDAO;

    @BeforeEach
    void setUp() {
        jugadorDAO = new JugadorDAOMemoria();
        partidaDAO = new PartidaDAOMemoria();
        historialDAO = new HistorialManoDAOMemoria();
        servicio = nuevoServicio();
        servicio.iniciarPartida(new ConfiguracionPartida("Juan", 1000, 10, 20, DificultadBot.MEDIO));
    }

    // Metodo para no repetir la creación del servicio con sus dobles de prueba
    private ServicioJuego nuevoServicio() {
        EscuchaEventosRegistro escucha = new EscuchaEventosRegistro();
        mesa = new Mesa(new Crupier("Crupier", new Baraja(new BarajadorAleatorio(new Random(11)))));
        return new ServicioJuego(mesa, new ServicioApuestas(new ValidadorAccion(), escucha), new ServicioShowdown(new EvaluadorManoEstandar()),
                new GestorBote(), jugadorDAO, partidaDAO, historialDAO, escucha, new ProveedorAccionSimulado());
    }

    // Metodo para sumar las fichas de toda la mesa
    private int fichasEnLaMesa() {
        int total = 0;
        for (Jugador jugador : mesa.getJugadores()) {
            total = total + jugador.getFichas();
        }
        return total;
    }

    // Iniciar la partida sienta 1 humano y 3 bots y los registra
    @Test
    void iniciarPartida() {
        assertEquals(4, mesa.getJugadores().size());
        assertInstanceOf(JugadorHumano.class, mesa.getJugadores().get(0));
        assertInstanceOf(JugadorBot.class, mesa.getJugadores().get(3));
        assertEquals(4, jugadorDAO.listarTodos().size(), "Los 4 jugadores quedan en la base de datos");
    }

    // Jugar una mano guarda el historial y no crea ni pierde fichas
    @Test
    void jugarMano() {
        HistorialManoDTO historial = servicio.jugarMano();
        assertNotNull(historial);
        assertEquals(1, historialDAO.listarTodos().size());
        assertFalse(historial.ganancias().isEmpty(), "Alguien debe ganar el bote");
        assertEquals(4000, fichasEnLaMesa());
        assertEquals(1, mesa.getPosicionBoton(), "El botón rota después de la mano");
    }

    // La partida termina cuando el humano se queda sin fichas
    @Test
    void partidaTerminada() {
        assertFalse(servicio.partidaTerminada());
        mesa.getJugadores().get(0).setFichas(0);
        assertTrue(servicio.partidaTerminada());
    }

    // Guardar la partida
    @Test
    void guardarPartida() {
        servicio.guardarPartida();
        assertEquals(1, partidaDAO.listarTodos().size());
        assertEquals(4, partidaDAO.buscarUltimaGuardada().get().jugadores().size());
    }

    // Cargar la última partida guardada
    @Test
    void cargarPartida() {
        mesa.getJugadores().get(0).setFichas(1300);
        servicio.guardarPartida();
        ServicioJuego otro = nuevoServicio();
        otro.cargarPartida(null);
        assertEquals(4, mesa.getJugadores().size());
        assertEquals(1300, mesa.getJugadores().get(0).getFichas());
    }

    // Las estadísticas se actualizan después de cada mano
    @Test
    void consultarEstadisticas() {
        servicio.jugarMano();
        JugadorDTO estadisticas = servicio.consultarEstadisticas("Juan");
        assertEquals(1, estadisticas.manosJugadas());
        assertNull(servicio.consultarEstadisticas("Nadie"));
    }

    // Obtener la mesa con los 4 asientos
    @Test
    void obtenerMesa() {
        assertEquals(4, servicio.obtenerMesa().asientos().size());
        assertEquals("Juan", servicio.obtenerMesa().asientos().get(0).nombre());
    }
}
