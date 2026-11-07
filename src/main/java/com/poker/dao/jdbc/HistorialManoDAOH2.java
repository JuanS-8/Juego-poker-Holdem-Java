package com.poker.dao.jdbc;

import com.poker.dao.ExcepcionAccesoDatos;
import com.poker.dao.HistorialManoDAO;
import com.poker.dto.AccionDTO;
import com.poker.dto.CartaDTO;
import com.poker.dto.HistorialManoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class HistorialManoDAOH2 extends DAOJdbcAbstracto<HistorialManoDTO, String> implements HistorialManoDAO {

    public HistorialManoDAOH2(ProveedorConexion proveedor) {
        super(proveedor);
    }

    @Override
    public void guardar(HistorialManoDTO dto) {
        if (dto == null || dto.id() == null)
            return;
        ejecutarActualizacion("INSERT INTO historial_mano (id, id_partida, numero_mano, comunitarias, bote, fecha) VALUES (?, ?, ?, ?, ?, ?)",
                dto.id(), dto.idPartida(), dto.numeroMano(), cartasATexto(dto.comunitarias()), dto.bote(), dto.fecha());
        guardarDetalle(dto);
    }

    @Override
    public Optional<HistorialManoDTO> buscarPorId(String id) {
        if (id == null)
            return Optional.empty();
        Optional<HistorialManoDTO> historial = consultarUno("SELECT * FROM historial_mano WHERE id = ?", id);
        if (historial.isEmpty())
            return historial;
        return Optional.of(conDetalle(historial.get()));
    }

    @Override
    public List<HistorialManoDTO> listarTodos() {
        List<HistorialManoDTO> datos = new ArrayList<>();
        for (HistorialManoDTO historial : consultarLista("SELECT * FROM historial_mano ORDER BY fecha")) {
            datos.add(conDetalle(historial));
        }
        return datos;
    }

    @Override
    public void actualizar(HistorialManoDTO dto) {
        if (dto == null || dto.id() == null)
            return;
        ejecutarActualizacion("UPDATE historial_mano SET id_partida = ?, numero_mano = ?, comunitarias = ?, bote = ?, fecha = ? WHERE id = ?",
                dto.idPartida(), dto.numeroMano(), cartasATexto(dto.comunitarias()), dto.bote(), dto.fecha(), dto.id());
        ejecutarActualizacion("DELETE FROM accion_mano WHERE id_historial = ?", dto.id());
        ejecutarActualizacion("DELETE FROM ganador_mano WHERE id_historial = ?", dto.id());
        guardarDetalle(dto);
    }

    @Override
    public boolean eliminar(String id) {
        if (id == null)
            return false;
        return ejecutarActualizacion("DELETE FROM historial_mano WHERE id = ?", id) > 0;
    }

    @Override
    public List<HistorialManoDTO> buscarPorPartida(String idPartida) {
        if (idPartida == null)
            return new ArrayList<>();
        List<HistorialManoDTO> datos = new ArrayList<>();
        for (HistorialManoDTO historial : consultarLista("SELECT * FROM historial_mano WHERE id_partida = ? ORDER BY numero_mano", idPartida)) {
            datos.add(conDetalle(historial));
        }
        return datos;
    }

    @Override
    protected HistorialManoDTO mapearFila(ResultSet resultado) throws SQLException {
        return new HistorialManoDTO(resultado.getString("id"), resultado.getString("id_partida"), resultado.getInt("numero_mano"), textoACartas(resultado.getString("comunitarias")),
                new ArrayList<>(), new LinkedHashMap<>(), resultado.getInt("bote"), resultado.getObject("fecha", LocalDateTime.class));
    }

    private void guardarDetalle(HistorialManoDTO dto) {
        // Guardar las acciones en orden
        if (dto.acciones() != null) {
            for (int orden = 0; orden < dto.acciones().size(); orden++) {
                AccionDTO accion = dto.acciones().get(orden);
                ejecutarActualizacion("INSERT INTO accion_mano (id_historial, orden, id_jugador, fase, tipo, monto) VALUES (?, ?, ?, ?, ?, ?)",
                        dto.id(), orden, accion.idJugador(), accion.fase(), accion.tipo(), accion.monto());
            }
        }
        // Guardar los ganadores
        if (dto.ganancias() != null) {
            for (String nombre : dto.ganancias().keySet()) {
                ejecutarActualizacion("INSERT INTO ganador_mano (id_historial, nombre_jugador, monto) VALUES (?, ?, ?)",
                        dto.id(), nombre, dto.ganancias().get(nombre));
            }
        }
    }


    private HistorialManoDTO conDetalle(HistorialManoDTO historial) {
        // Consultar las acciones y los ganadores de la mano
        List<AccionDTO> acciones = new ArrayList<>();
        Map<String, Integer> ganancias = new LinkedHashMap<>();
        try (Connection conexion = proveedor.obtenerConexion(); PreparedStatement consultaAcciones = conexion.prepareStatement("SELECT * FROM accion_mano WHERE id_historial = ? ORDER BY orden");
                PreparedStatement consultaGanadores = conexion.prepareStatement("SELECT * FROM ganador_mano WHERE id_historial = ?")) {
            consultaAcciones.setString(1, historial.id());
            try (ResultSet resultado = consultaAcciones.executeQuery()) {
                while (resultado.next()) {
                    acciones.add(new AccionDTO(resultado.getString("id_jugador"), resultado.getString("fase"),
                            resultado.getString("tipo"), resultado.getInt("monto")));
                }
            }
            consultaGanadores.setString(1, historial.id());
            try (ResultSet resultado = consultaGanadores.executeQuery()) {
                while (resultado.next()) {
                    ganancias.put(resultado.getString("nombre_jugador"), resultado.getInt("monto"));
                }
            }
        } catch (SQLException sqlException) {
            throw new ExcepcionAccesoDatos("Se genero error al consultar el detalle de la mano " + sqlException.getMessage(), sqlException);
        }
        // Devolver el historial completo
        return new HistorialManoDTO(historial.id(), historial.idPartida(), historial.numeroMano(),
                historial.comunitarias(), acciones, ganancias, historial.bote(), historial.fecha());
    }

    private String cartasATexto(List<CartaDTO> cartas) {
        // Las cartas se guardan como texto
        if (cartas == null)
            return "";
        String texto = "";
        for (CartaDTO carta : cartas) {
            texto = texto + carta.valor() + "-" + carta.palo() + " ";
        }
        return texto.trim();
    }

    private List<CartaDTO> textoACartas(String texto) {
        List<CartaDTO> cartas = new ArrayList<>();
        if (texto == null || texto.isBlank())
            return cartas;
        // Separar el texto en cartas y cada carta en valor y palo
        for (String parte : texto.trim().split(" ")) {
            String[] datos = parte.split("-");
            cartas.add(new CartaDTO(datos[0], datos[1]));
        }
        return cartas;
    }
}
