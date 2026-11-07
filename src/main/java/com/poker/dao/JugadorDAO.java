package com.poker.dao;

import com.poker.dto.JugadorDTO;

import java.util.Optional;

public interface JugadorDAO extends DAOGenerico<JugadorDTO, String> {
    Optional<JugadorDTO> buscarPorNombre(String nombre);
}
