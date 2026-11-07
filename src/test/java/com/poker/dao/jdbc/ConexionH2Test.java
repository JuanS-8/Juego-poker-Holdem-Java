package com.poker.dao.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ConexionH2Test {
    private final ProveedorConexion proveedor = new ProveedorConexionH2("jdbc:h2:mem:" + UUID.randomUUID()
            + ";DB_CLOSE_DELAY=-1", "sa", "");

    // Obtener una conexión abierta
    @Test
    void obtenerConexion() throws SQLException {
        try (Connection conexion = proveedor.obtenerConexion()) {
            assertFalse(conexion.isClosed());
        }
    }

    // Crear las 6 tablas del schema.sql
    @Test
    void crearTablas() throws SQLException {
        new InicializadorBD(proveedor).crearTablas();
        try (Connection conexion = proveedor.obtenerConexion();
                ResultSet tablas = conexion.createStatement().executeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'")) {
            tablas.next();
            assertEquals(6, tablas.getInt(1));
        }
    }
}
