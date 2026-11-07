package com.poker.dto;

import java.io.Serializable;
import java.util.List;

public record AsientoDTO(String nombre, int fichas, int apuestaActual, String estado,
        List<CartaDTO> cartasVisibles) implements Serializable {
}
