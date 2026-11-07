package com.poker.dto;

import java.io.Serializable;
import java.util.List;

public record EstadoPartidaDTO(String idPartida, List<JugadorDTO> jugadores, int posicionBoton, int ciegaPequena,
        int ciegaGrande, int numeroMano) implements Serializable {
}
