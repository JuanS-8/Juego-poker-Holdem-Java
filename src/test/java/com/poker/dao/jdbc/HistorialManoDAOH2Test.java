package com.poker.dao.jdbc;

import com.poker.dto.AccionDTO;
import com.poker.dto.CartaDTO;
import com.poker.dto.HistorialManoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class HistorialManoDAOH2Test {
    private HistorialManoDAOH2 dao;

    @BeforeEach
    void setUp() {
        ProveedorConexion proveedor = new ProveedorConexionH2("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1", "sa", "");
        new InicializadorBD(proveedor).crearTablas();
        dao = new HistorialManoDAOH2(proveedor);
        dao.guardar(nuevoHistorial("h1", "p1", 1));
    }

    // Metodo para no repetir los datos del historial
    private HistorialManoDTO nuevoHistorial(String id, String idPartida, int numeroMano) {
        List<CartaDTO> comunitarias = List.of(new CartaDTO("AS", "PICAS"), new CartaDTO("DIEZ", "CORAZONES"),
                new CartaDTO("DOS", "TREBOLES"));
        List<AccionDTO> acciones = List.of(new AccionDTO("1", "PRE_FLOP", "SUBIR", 40), new AccionDTO("2", "PRE_FLOP",
                "RETIRARSE", 0));
        return new HistorialManoDTO(id, idPartida, numeroMano, comunitarias, acciones, Map.of("Ana", 70), 70,
                LocalDateTime.of(2026, 11, 3, 10, 0));
    }

    // Guardar un historial
    @Test
    void guardar() {
        dao.guardar(nuevoHistorial("h2", "p1", 2));
        assertEquals(2, dao.listarTodos().size());
    }

    // Buscar por id con cartas, acciones y ganadores
    @Test
    void buscarPorId() {
        HistorialManoDTO historial = dao.buscarPorId("h1").get();
        assertEquals(new CartaDTO("DIEZ", "CORAZONES"), historial.comunitarias().get(1));
        assertEquals("SUBIR", historial.acciones().get(0).tipo(), "Las acciones vuelven en orden");
        assertEquals(70, historial.ganancias().get("Ana"));
    }

    // Listar todos
    @Test
    void listarTodos() {
        assertEquals(1, dao.listarTodos().size());
    }

    // Actualizar sin duplicar acciones
    @Test
    void actualizar() {
        dao.actualizar(nuevoHistorial("h1", "p1", 7));
        assertEquals(7, dao.buscarPorId("h1").get().numeroMano());
        assertEquals(2, dao.buscarPorId("h1").get().acciones().size());
    }

    // Eliminar
    @Test
    void eliminar() {
        assertTrue(dao.eliminar("h1"));
        assertTrue(dao.buscarPorId("h1").isEmpty());
    }

    // Buscar las manos de una partida
    @Test
    void buscarPorPartida() {
        dao.guardar(nuevoHistorial("h2", "p2", 1));
        assertEquals(1, dao.buscarPorPartida("p1").size());
    }
}
