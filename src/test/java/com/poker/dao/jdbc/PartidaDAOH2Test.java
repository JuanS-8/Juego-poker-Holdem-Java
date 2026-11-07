package com.poker.dao.jdbc;

import com.poker.dto.EstadoPartidaDTO;
import com.poker.dto.JugadorDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PartidaDAOH2Test {
    private PartidaDAOH2 dao;

    @BeforeEach
    void setUp() {
        ProveedorConexion proveedor = new ProveedorConexionH2("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1", "sa", "");
        new InicializadorBD(proveedor).crearTablas();
        JugadorDAOH2 jugadores = new JugadorDAOH2(proveedor);
        jugadores.guardar(new JugadorDTO("1", "Ana", 1000, false, 0, 0, 1000));
        jugadores.guardar(new JugadorDTO("2", "Bot Luis", 1000, true, 0, 0, 1000));
        dao = new PartidaDAOH2(proveedor);
        dao.guardar(nuevaPartida("p1", 5));
    }

    // Metodo para no repetir los datos de la partida
    private EstadoPartidaDTO nuevaPartida(String id, int numeroMano) {
        JugadorDTO ana = new JugadorDTO("1", "Ana", 1300, false, 0, 0, 0);
        JugadorDTO bot = new JugadorDTO("2", "Bot Luis", 700, true, 0, 0, 0);
        return new EstadoPartidaDTO(id, List.of(ana, bot), 1, 10, 20, numeroMano);
    }

    // Guardar una partida
    @Test
    void guardar() {
        dao.guardar(nuevaPartida("p2", 1));
        assertEquals(2, dao.listarTodos().size());
    }

    // Buscar por id con sus jugadores y fichas
    @Test
    void buscarPorId() {
        EstadoPartidaDTO partida = dao.buscarPorId("p1").get();
        assertEquals(5, partida.numeroMano());
        assertEquals(1300, partida.jugadores().get(0).fichas(), "Se guardan las fichas que tenía en la partida");
        assertTrue(dao.buscarPorId("999").isEmpty());
    }

    // Listar todas
    @Test
    void listarTodos() {
        assertEquals(1, dao.listarTodos().size());
    }

    // Actualizar sin duplicar jugadores
    @Test
    void actualizar() {
        dao.actualizar(nuevaPartida("p1", 9));
        assertEquals(9, dao.buscarPorId("p1").get().numeroMano());
        assertEquals(2, dao.buscarPorId("p1").get().jugadores().size());
    }

    // Eliminar
    @Test
    void eliminar() {
        assertTrue(dao.eliminar("p1"));
        assertFalse(dao.eliminar("p1"));
    }

    // Buscar la última guardada
    @Test
    void buscarUltimaGuardada() throws InterruptedException {
        Thread.sleep(20);
        dao.guardar(nuevaPartida("p2", 1));
        assertEquals("p2", dao.buscarUltimaGuardada().get().idPartida());
    }
}
