package com.poker.dao;

import com.poker.dto.HistorialManoDTO;

import java.util.List;

public interface HistorialManoDAO extends DAOGenerico<HistorialManoDTO, String> {
    List<HistorialManoDTO> buscarPorPartida(String idPartida);
}
