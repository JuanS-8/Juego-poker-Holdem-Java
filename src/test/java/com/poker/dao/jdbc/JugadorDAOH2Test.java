package com.poker.dao.jdbc;

import com.poker.dto.JugadorDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JugadorDAOH2Test {
    private JugadorDAOH2 dao;

    @BeforeEach
    void setUp() {
        ProveedorConexion proveedor = new ProveedorConexionH2("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1", "sa", "");
        new InicializadorBD(proveedor).crearTablas();
        dao = new JugadorDAOH2(proveedor);
        dao.guardar(nuevoJugador("1", "Ana"));
    }

    // Metodo para no repetir los datos del jugador
    private JugadorDTO nuevoJugador(String id, String nombre) {
        return new JugadorDTO(id, nombre, 1000, false, 0, 0, 1000);
    }

    // Guardar un jugador
    @Test
    void guardar() {
        int inicial = dao.listarTodos().size();
        dao.guardar(nuevoJugador("2", "Luis"));
        dao.guardar(null);
        assertEquals(inicial + 1, dao.listarTodos().size(), "Error al almacenar");
    }

    // Buscar por id existente e inexistente
    @Test
    void buscarPorId() {
        assertEquals("Ana", dao.buscarPorId("1").get().nombre());
        assertTrue(dao.buscarPorId("999").isEmpty());
    }

    // Listar todos
    @Test
    void listarTodos() {
        dao.guardar(nuevoJugador("2", "Luis"));
        assertEquals(2, dao.listarTodos().size());
    }

    // Actualizar las estadísticas
    @Test
    void actualizar() {
        dao.actualizar(new JugadorDTO("1", "Ana", 1500, false, 3, 1, 1500));
        assertEquals(3, dao.buscarPorId("1").get().manosJugadas());
    }

    // Eliminar existente e inexistente
    @Test
    void eliminar() {
        assertTrue(dao.eliminar("1"), "Debe devolver true al eliminar");
        assertFalse(dao.eliminar("999"), "Debe devolver false si no existe");
    }

    // Buscar por nombre
    @Test
    void buscarPorNombre() {
        assertTrue(dao.buscarPorNombre("Ana").isPresent());
        assertTrue(dao.buscarPorNombre("Luis").isEmpty());
    }
}
