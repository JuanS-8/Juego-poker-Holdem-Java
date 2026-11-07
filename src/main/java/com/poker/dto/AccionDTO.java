package com.poker.dto;

import java.io.Serializable;

public record AccionDTO(String idJugador, String fase, String tipo, int monto) implements Serializable {
}
