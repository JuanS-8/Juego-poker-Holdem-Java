package com.poker.dao.jdbc;

import com.poker.dao.DAOGenerico;
import com.poker.dao.ExcepcionAccesoDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class DAOJdbcAbstracto<T, ID> implements DAOGenerico<T, ID> {
    protected ProveedorConexion proveedor;

    protected DAOJdbcAbstracto(ProveedorConexion proveedor) {
        this.proveedor = proveedor;
    }

    protected int ejecutarActualizacion(String sql, Object... parametros) {
        try (Connection conexion = proveedor.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            // Asignar los parámetros a la sentencia
            for (int i = 0; i < parametros.length; i++) {
                sentencia.setObject(i + 1, parametros[i]);
            }
            // Ejecutar
            return sentencia.executeUpdate();
        } catch (SQLException sqlException) {
            throw new ExcepcionAccesoDatos("Se genero error al guardar " + sqlException.getMessage(), sqlException);
        }
    }

    protected List<T> consultarLista(String sql, Object... parametros) {
        List<T> datos = new ArrayList<>();
        try (Connection conexion = proveedor.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            // Asignar los parámetros a la sentencia
            for (int i = 0; i < parametros.length; i++) {
                sentencia.setObject(i + 1, parametros[i]);
            }
            // Convertir cada fila en un DTO
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    datos.add(mapearFila(resultado));
                }
            }
        } catch (SQLException sqlException) {
            throw new ExcepcionAccesoDatos("Se genero error al consultar " + sqlException.getMessage(), sqlException);
        }
        return datos;
    }

    protected Optional<T> consultarUno(String sql, Object... parametros) {
        List<T> datos = consultarLista(sql, parametros);
        if (datos.isEmpty())
            return Optional.empty();
        return Optional.of(datos.get(0));
    }

    protected abstract T mapearFila(ResultSet resultado) throws SQLException;

}
