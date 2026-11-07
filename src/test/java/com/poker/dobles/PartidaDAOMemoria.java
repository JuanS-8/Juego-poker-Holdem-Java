package com.poker.dobles;

import com.poker.dao.PartidaDAO;
import com.poker.dto.EstadoPartidaDTO;

import java.util.*;

public class PartidaDAOMemoria implements PartidaDAO {
    private final Map<String, EstadoPartidaDTO> datos = new LinkedHashMap<>();
    private EstadoPartidaDTO ultima;

    @Override
    public void guardar(EstadoPartidaDTO dto) {
        if (dto == null)
            return;
        datos.put(dto.idPartida(), dto);
        ultima = dto;
    }

    @Override
    public Optional<EstadoPartidaDTO> buscarPorId(String id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public List<EstadoPartidaDTO> listarTodos() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public void actualizar(EstadoPartidaDTO dto) {
        if (dto != null && datos.containsKey(dto.idPartida())) {
            datos.put(dto.idPartida(), dto);
            ultima = dto;
        }
    }

    @Override
    public boolean eliminar(String id) {
        return datos.remove(id) != null;
    }

    @Override
    public Optional<EstadoPartidaDTO> buscarUltimaGuardada() {
        return Optional.ofNullable(ultima);
    }
}
