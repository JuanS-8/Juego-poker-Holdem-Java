package com.poker.dao.jdbc;

import com.poker.dao.JugadorDAO;
import com.poker.dto.JugadorDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JugadorDAOH2 extends DAOJdbcAbstracto<JugadorDTO, String> implements JugadorDAO {

    public JugadorDAOH2(ProveedorConexion proveedor) {
        super(proveedor);
    }

    @Override
    public void guardar(JugadorDTO dto) {
        if (dto == null)
            return;
        ejecutarActualizacion("INSERT INTO jugador (id, nombre, es_bot, fichas, manos_jugadas, manos_ganadas, maximo_fichas) VALUES (?, ?, ?, ?, ?, ?, ?)",
                dto.id(), dto.nombre(), dto.esBot(), dto.fichas(), dto.manosJugadas(), dto.manosGanadas(), dto.maximoFichas());
    }

    @Override
    public Optional<JugadorDTO> buscarPorId(String id) {
        if (id == null)
            return Optional.empty();
        return consultarUno("SELECT * FROM jugador WHERE id = ?", id);
    }

    @Override
    public List<JugadorDTO> listarTodos() {
        return consultarLista("SELECT * FROM jugador ORDER BY nombre");
    }

    @Override
    public void actualizar(JugadorDTO dto) {
        if (dto == null)
            return;
        ejecutarActualizacion("UPDATE jugador SET nombre = ?, es_bot = ?, fichas = ?, manos_jugadas = ?, manos_ganadas = ?, maximo_fichas = ? WHERE id = ?",
                dto.nombre(), dto.esBot(), dto.fichas(), dto.manosJugadas(), dto.manosGanadas(), dto.maximoFichas(), dto.id());
    }

    @Override
    public boolean eliminar(String id) {
        if (id == null)
            return false;
        return ejecutarActualizacion("DELETE FROM jugador WHERE id = ?", id) > 0;
    }

    @Override
    public Optional<JugadorDTO> buscarPorNombre(String nombre) {
        if (nombre == null)
            return Optional.empty();
        return consultarUno("SELECT * FROM jugador WHERE nombre = ?", nombre);
    }

    @Override
    protected JugadorDTO mapearFila(ResultSet resultado) throws SQLException {
        return new JugadorDTO(resultado.getString("id"), resultado.getString("nombre"), resultado.getInt("fichas"), resultado.getBoolean("es_bot"),
                resultado.getInt("manos_jugadas"), resultado.getInt("manos_ganadas"), resultado.getInt("maximo_fichas"));
    }
}
