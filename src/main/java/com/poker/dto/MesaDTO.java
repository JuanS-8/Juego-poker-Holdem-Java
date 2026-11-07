package com.poker.dto;

import java.io.Serializable;
import java.util.List;

public record MesaDTO(String fase, int bote, List<CartaDTO> comunitarias,
        List<AsientoDTO> asientos) implements Serializable {
}
