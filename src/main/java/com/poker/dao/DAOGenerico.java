package com.poker.dao;

import java.util.List;
import java.util.Optional;

public interface DAOGenerico<T, ID> {
    void guardar(T dto);
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
    void actualizar(T dto);
    boolean eliminar(ID id);
}
