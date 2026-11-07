package com.poker.dao;

import com.poker.dto.EstadoPartidaDTO;

import java.util.Optional;

public interface PartidaDAO extends DAOGenerico<EstadoPartidaDTO, String> {
    Optional<EstadoPartidaDTO> buscarUltimaGuardada();
}
