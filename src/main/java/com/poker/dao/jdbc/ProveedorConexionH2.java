package com.poker.dao.jdbc;

import com.poker.dao.ExcepcionAccesoDatos;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ProveedorConexionH2 implements ProveedorConexion {
    private String url;
    private String usuario;
    private String clave;

    public ProveedorConexionH2() {
        // Leer database.properties
        Properties propiedades = new Properties();
        try (InputStream archivo = getClass().getResourceAsStream("/database.properties")) {
            if (archivo != null) {
                propiedades.load(archivo);
            }
        } catch (IOException ioException) {
            System.err.println("Se genero error al leer database.properties " + ioException.getMessage());
        }
        // Si falta un dato se usa el valor por defecto
        this.url = propiedades.getProperty("db.url", "jdbc:h2:./data/pokerdb");
        this.usuario = propiedades.getProperty("db.user", "sa");
        this.clave = propiedades.getProperty("db.password", "");
    }

    public ProveedorConexionH2(String url, String usuario, String clave) {
        this.url = url;
        this.usuario = usuario;
        this.clave = clave;
    }

    @Override
    public Connection obtenerConexion() {
        try {
            return DriverManager.getConnection(url, usuario, clave);
        } catch (SQLException sqlException) {
            throw new ExcepcionAccesoDatos("Se genero error al conectar con la base de datos " + sqlException.getMessage(), sqlException);
        }
    }
}
