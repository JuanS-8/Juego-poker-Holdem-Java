package com.poker.dobles;

import com.poker.dao.JugadorDAO;
import com.poker.dto.JugadorDTO;

import java.util.*;

public class JugadorDAOMemoria implements JugadorDAO {
    private final Map<String, JugadorDTO> datos = new LinkedHashMap<>();

    @Override
    public void guardar(JugadorDTO dto) {
        if (dto == null)
            return;
        datos.put(dto.id(), dto);
    }

    @Override
    public Optional<JugadorDTO> buscarPorId(String id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public List<JugadorDTO> listarTodos() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public void actualizar(JugadorDTO dto) {
        if (dto != null && datos.containsKey(dto.id())) {
            datos.put(dto.id(), dto);
        }
    }

    @Override
    public boolean eliminar(String id) {
        return datos.remove(id) != null;
    }

    @Override
    public Optional<JugadorDTO> buscarPorNombre(String nombre) {
        for (JugadorDTO dto : datos.values()) {
            if (dto.nombre().equals(nombre))
                return Optional.of(dto);
        }
        return Optional.empty();
    }
}
