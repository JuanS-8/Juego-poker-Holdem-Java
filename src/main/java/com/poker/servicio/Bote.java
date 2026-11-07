package com.poker.servicio;

import com.poker.modelo.Jugador;

import java.util.Set;

public record Bote(int monto, Set<Jugador> elegibles) {
}
