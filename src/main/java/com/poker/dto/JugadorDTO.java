package com.poker.dto;

import java.io.Serializable;

public record JugadorDTO(String id, String nombre, int fichas, boolean esBot, int manosJugadas, int manosGanadas,
        int maximoFichas) implements Serializable {
}
