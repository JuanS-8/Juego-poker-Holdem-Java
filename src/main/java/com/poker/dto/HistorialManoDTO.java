package com.poker.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record HistorialManoDTO(String id, String idPartida, int numeroMano, List<CartaDTO> comunitarias,
        List<AccionDTO> acciones, Map<String, Integer> ganancias, int bote, LocalDateTime fecha) implements Serializable {
}
