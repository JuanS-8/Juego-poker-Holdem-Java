package com.poker.modelo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Jugador {
    private String id;
    private String nombre;
    private int fichas;
    private List<Carta> cartas;
    private int apuestaActual;
    private EstadoJugador estado;

    protected Jugador(String id, String nombre, int fichas) {
        this.id = id;
        this.nombre = nombre;
        this.fichas = fichas;
        this.cartas = new ArrayList<>();
        this.apuestaActual = 0;
        if (fichas > 0) {
            this.estado = EstadoJugador.ACTIVO;
        } else {
            this.estado = EstadoJugador.ELIMINADO;
        }
    }

    public int apostar(int monto) {
        // Validar el monto
        if (monto <= 0)
            return 0;
        // Nunca apostar más fichas de las que tiene
        int apostado = monto;
        if (apostado > fichas) {
            apostado = fichas;
        }
        // Pasar las fichas a la apuesta
        fichas = fichas - apostado;
        apuestaActual = apuestaActual + apostado;
        // Si se quedó sin fichas queda en all-in
        if (fichas == 0) {
            estado = EstadoJugador.ALL_IN;
        }
        return apostado;
    }

    public void retirarse() {
        estado = EstadoJugador.RETIRADO;
    }

    public void recibirCarta(Carta carta) {
        // Máximo 2 cartas propias
        if (carta == null || cartas.size() >= 2)
            return;
        cartas.add(carta);
    }

    public void limpiarMano() {
        // Quitar las cartas y la apuesta de la mano anterior
        cartas.clear();
        apuestaActual = 0;
        // Si no tiene fichas queda eliminado
        if (fichas > 0) {
            estado = EstadoJugador.ACTIVO;
        } else {
            estado = EstadoJugador.ELIMINADO;
        }
    }

    public abstract Accion decidirAccion(ContextoDecision contexto);
}
