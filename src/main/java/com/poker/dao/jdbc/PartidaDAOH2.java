package com.poker.dao.jdbc;

import com.poker.dao.ExcepcionAccesoDatos;
import com.poker.dao.PartidaDAO;
import com.poker.dto.EstadoPartidaDTO;
import com.poker.dto.JugadorDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PartidaDAOH2 extends DAOJdbcAbstracto<EstadoPartidaDTO, String> implements PartidaDAO {

    public PartidaDAOH2(ProveedorConexion proveedor) {
        super(proveedor);
    }

    @Override
    public void guardar(EstadoPartidaDTO dto) {
        if (dto == null || dto.idPartida() == null)
            return;
        // Guardar los datos de la partida
        ejecutarActualizacion("INSERT INTO partida (id, posicion_boton, ciega_pequena, ciega_grande, numero_mano, fecha_guardado) VALUES (?, ?, ?, ?, ?, ?)",
                dto.idPartida(), dto.posicionBoton(), dto.ciegaPequena(), dto.ciegaGrande(), dto.numeroMano(), LocalDateTime.now());
        // Guardar los jugadores con sus fichas
        guardarJugadores(dto);
    }

    @Override
    public Optional<EstadoPartidaDTO> buscarPorId(String id) {
        if (id == null)
            return Optional.empty();
        Optional<EstadoPartidaDTO> partida = consultarUno("SELECT * FROM partida WHERE id = ?", id);
        if (partida.isEmpty())
            return partida;
        return Optional.of(conJugadores(partida.get()));
    }

    @Override
    public List<EstadoPartidaDTO> listarTodos() {
        List<EstadoPartidaDTO> datos = new ArrayList<>();
        for (EstadoPartidaDTO partida : consultarLista("SELECT * FROM partida ORDER BY fecha_guardado DESC")) {
            datos.add(conJugadores(partida));
        }
        return datos;
    }

    @Override
    public void actualizar(EstadoPartidaDTO dto) {
        if (dto == null || dto.idPartida() == null)
            return;
        // Actualizar los datos de la partida
        ejecutarActualizacion("UPDATE partida SET posicion_boton = ?, ciega_pequena = ?, ciega_grande = ?, numero_mano = ?, fecha_guardado = ? WHERE id = ?",
                dto.posicionBoton(), dto.ciegaPequena(), dto.ciegaGrande(), dto.numeroMano(), LocalDateTime.now(), dto.idPartida());
        // Reemplazar los jugadores
        ejecutarActualizacion("DELETE FROM partida_jugador WHERE id_partida = ?", dto.idPartida());
        guardarJugadores(dto);
    }

    @Override
    public boolean eliminar(String id) {
        if (id == null)
            return false;
        return ejecutarActualizacion("DELETE FROM partida WHERE id = ?", id) > 0;
    }

    @Override
    public Optional<EstadoPartidaDTO> buscarUltimaGuardada() {
        Optional<EstadoPartidaDTO> partida = consultarUno("SELECT * FROM partida ORDER BY fecha_guardado DESC LIMIT 1");
        if (partida.isEmpty())
            return partida;
        return Optional.of(conJugadores(partida.get()));
    }

    @Override
    protected EstadoPartidaDTO mapearFila(ResultSet resultado) throws SQLException {
        return new EstadoPartidaDTO(resultado.getString("id"), new ArrayList<>(), resultado.getInt("posicion_boton"),
                resultado.getInt("ciega_pequena"), resultado.getInt("ciega_grande"), resultado.getInt("numero_mano"));
    }

    private void guardarJugadores(EstadoPartidaDTO dto) {
        if (dto.jugadores() == null)
            return;
        for (int asiento = 0; asiento < dto.jugadores().size(); asiento++) {
            JugadorDTO jugador = dto.jugadores().get(asiento);
            ejecutarActualizacion("INSERT INTO partida_jugador (id_partida, id_jugador, asiento, fichas) VALUES (?, ?, ?, ?)",
                    dto.idPartida(), jugador.id(), asiento, jugador.fichas());
        }
    }

    private EstadoPartidaDTO conJugadores(EstadoPartidaDTO partida) {
        // Consultar los jugadores de la partida en el orden de los asientos
        List<JugadorDTO> jugadores = new ArrayList<>();
        String sql = "SELECT j.id, j.nombre, j.es_bot, pj.fichas, j.manos_jugadas, j.manos_ganadas, j.maximo_fichas "
                + "FROM partida_jugador pj JOIN jugador j ON j.id = pj.id_jugador WHERE pj.id_partida = ? ORDER BY pj.asiento";
        try (Connection conexion = proveedor.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, partida.idPartida());
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    jugadores.add(new JugadorDTO(resultado.getString("id"), resultado.getString("nombre"), resultado.getInt("fichas"),
                            resultado.getBoolean("es_bot"), resultado.getInt("manos_jugadas"), resultado.getInt("manos_ganadas"), resultado.getInt("maximo_fichas")));
                }
            }
        } catch (SQLException sqlException) {
            throw new ExcepcionAccesoDatos("Se genero error al consultar los jugadores de la partida " + sqlException.getMessage(), sqlException);
        }
        // Devolver la partida con sus jugadores
        return new EstadoPartidaDTO(partida.idPartida(), jugadores, partida.posicionBoton(), partida.ciegaPequena(),
                partida.ciegaGrande(), partida.numeroMano());
    }
}
