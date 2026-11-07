package com.poker.dao.jdbc;

import com.poker.dao.ExcepcionAccesoDatos;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InicializadorBD {
    private ProveedorConexion proveedor;

    public InicializadorBD(ProveedorConexion proveedor) {
        this.proveedor = proveedor;
    }

    public void crearTablas() {
        // Leer el archivo schema.sql sin las líneas de comentario
        String script = "";
        try (InputStream archivo = getClass().getResourceAsStream("/schema.sql")) {
            if (archivo == null)
                throw new ExcepcionAccesoDatos("No se encontro el archivo schema.sql", null);
            for (String linea : new String(archivo.readAllBytes(), StandardCharsets.UTF_8).split("\n")) {
                if (!linea.trim().startsWith("--")) {
                    script = script + linea + "\n";
                }
            }
        } catch (IOException ioException) {
            throw new ExcepcionAccesoDatos("Se genero error al leer schema.sql " + ioException.getMessage(), ioException);
        }

        // Ejecutar cada sentencia CREATE TABLE
        try (Connection conexion = proveedor.obtenerConexion(); Statement sentencia = conexion.createStatement()) {
            for (String instruccion : script.split(";")) {
                if (!instruccion.isBlank()) {
                    sentencia.execute(instruccion);
                }
            }
        } catch (SQLException sqlException) {
            throw new ExcepcionAccesoDatos("Se genero error al crear las tablas " + sqlException.getMessage(), sqlException);
        }
    }
}
