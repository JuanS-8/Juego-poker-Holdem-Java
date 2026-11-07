package com.poker.mapeador;

import com.poker.dto.JugadorDTO;
import com.poker.modelo.Jugador;
import com.poker.modelo.JugadorBot;
import com.poker.modelo.JugadorHumano;

public class JugadorMapper {

    public JugadorDTO aDTO(Jugador jugador) {
        if (jugador == null)
            return null;
        boolean esBot = jugador instanceof JugadorBot;
        return new JugadorDTO(jugador.getId(), jugador.getNombre(), jugador.getFichas(), esBot, 0, 0,
                jugador.getFichas());
    }

    public Jugador aEntidad(JugadorDTO dto) {
        if (dto == null)
            return null;
        // Crear el tipo de jugador según el DTO
        if (dto.esBot()) {
            return new JugadorBot(dto.id(), dto.nombre(), dto.fichas(), null);
        }
        return new JugadorHumano(dto.id(), dto.nombre(), dto.fichas(), null);
    }
}
