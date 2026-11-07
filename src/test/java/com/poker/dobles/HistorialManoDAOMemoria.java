package com.poker.dobles;

import com.poker.dao.HistorialManoDAO;
import com.poker.dto.HistorialManoDTO;

import java.util.*;

public class HistorialManoDAOMemoria implements HistorialManoDAO {
    private final Map<String, HistorialManoDTO> datos = new LinkedHashMap<>();

    @Override
    public void guardar(HistorialManoDTO dto) {
        if (dto == null)
            return;
        datos.put(dto.id(), dto);
    }

    @Override
    public Optional<HistorialManoDTO> buscarPorId(String id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public List<HistorialManoDTO> listarTodos() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public void actualizar(HistorialManoDTO dto) {
        if (dto != null && datos.containsKey(dto.id())) {
            datos.put(dto.id(), dto);
        }
    }

    @Override
    public boolean eliminar(String id) {
        return datos.remove(id) != null;
    }

    @Override
    public List<HistorialManoDTO> buscarPorPartida(String idPartida) {
        List<HistorialManoDTO> resultado = new ArrayList<>();
        for (HistorialManoDTO dto : datos.values()) {
            if (dto.idPartida().equals(idPartida))
                resultado.add(dto);
        }
        return resultado;
    }
}
